package com.megacart.service.impl;

import com.megacart.dto.projection.SoLuongDonHangTheoTrangThai;
import com.megacart.model.ThongKeNgay;
import com.megacart.model.ThongKeThang;
import com.megacart.repository.ChiTietDonHangRepository;
import com.megacart.repository.SanPhamRepository;
import com.megacart.repository.ThongKeNgayRepository;
import com.megacart.repository.ThongKeThangRepository;
import com.megacart.service.ThongKeSchedulerService;
import com.megacart.enumeration.TrangThaiDonHang;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Sử dụng Slf4j để ghi log, chuyên nghiệp hơn System.out.println
public class ThongKeSchedulerServiceImpl implements ThongKeSchedulerService {

    private final ThongKeThangRepository thongKeThangRepository;
    private final ThongKeNgayRepository thongKeNgayRepository;
    private final com.megacart.repository.DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final SanPhamRepository sanPhamRepository;

    private static final int TOP_N_PRODUCTS = 10; // Top 10 sản phẩm
    private static final int LOOKBACK_DAYS = 30; // Trong vòng 30 ngày

    /**
     * Tác vụ này chạy vào 00:01 mỗi ngày.
     * Mục đích: Đảm bảo rằng bản ghi thống kê cho ngày hôm nay và tháng này đã tồn tại.
     * Điều này giúp các chức năng khác (như cập nhật mục tiêu) có thể hoạt động ngay từ đầu ngày/tháng
     * mà không cần kiểm tra sự tồn tại của bản ghi.
     */
    @Override
    @Scheduled(cron = "0 1 0 * * ?") // Chạy vào 00:01:00 mỗi ngày
    @Transactional
    public void ensureThongKeRecordsExist() {
        log.info("Bắt đầu tác vụ kiểm tra và tạo bản ghi thống kê...");
        LocalDate today = LocalDate.now();

        // Đảm bảo bản ghi của tháng tồn tại
        ensureThongKeThangRecordExists(today);

        // Đảm bảo bản ghi của ngày tồn tại
        ensureThongKeNgayRecordExists(today);

        log.info("Hoàn tất tác vụ kiểm tra và tạo bản ghi thống kê.");
    }

    private void ensureThongKeThangRecordExists(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        thongKeThangRepository.findByNamAndThang(yearMonth.getYear(), yearMonth.getMonthValue())
                .orElseGet(() -> {
                    log.info("Chưa có bản ghi thống kê cho tháng {}/{}. Đang tạo mới...", yearMonth.getMonthValue(), yearMonth.getYear());
                    ThongKeThang newThongKeThang = ThongKeThang.builder()
                            .nam(yearMonth.getYear()).thang(yearMonth.getMonthValue()).doanhThu(0).tangTruongDoanhThu(0.0)
                            .soDonHang(0).tangTruongDonHang(0.0).soSanPham(0)
                            .trungBinhMoiDon(BigDecimal.ZERO).donChoXacNhan(0)
                            .donChoXuLi(0).donDangGiao(0).donDaGiao(0).daDaHuy(0)
                            .mucTieuDoanhThu(0).build();
                    return thongKeThangRepository.save(newThongKeThang);
                });
    }

    private void ensureThongKeNgayRecordExists(LocalDate date) {
        thongKeNgayRepository.findByNgay(date).orElseGet(() -> {
            log.info("Chưa có bản ghi thống kê cho ngày {}. Đang tạo mới...", date);
            ThongKeNgay newThongKeNgay = ThongKeNgay.builder()
                    .ngay(date).tongDoanhThu(0).soDon(0).soSanPham(0).build();
            return thongKeNgayRepository.save(newThongKeNgay);
        });
    }

    /**
     * Tác vụ này chạy vào 00:05 mỗi ngày để tổng hợp dữ liệu của ngày hôm qua.
     * Chạy sau tác vụ ensureThongKeRecordsExist để đảm bảo bản ghi luôn tồn tại.
     */
    @Override
    @Scheduled(cron = "0 5 0 * * ?") // Chạy vào 00:05:00 mỗi ngày
    @Transactional
    public void aggregateDailyStatistics() {
        LocalDate homQua = LocalDate.now().minusDays(1);
        log.info("Bắt đầu tác vụ tổng hợp dữ liệu thống kê cho ngày: {}", homQua);

        // --- Tổng hợp cho ThongKeNgay ---
        thongKeNgayRepository.findByNgay(homQua).ifPresent(tkNgay -> {
            LocalDateTime startOfYesterday = homQua.atStartOfDay();
            LocalDateTime endOfYesterday = homQua.atTime(LocalTime.MAX);

            long doanhThuNgay = donHangRepository.sumRevenueByStatusAndTimeRange(TrangThaiDonHang.DA_GIAO, startOfYesterday, endOfYesterday);
            long soDonNgay = donHangRepository.countByThoiGianDatHangBetween(startOfYesterday, endOfYesterday);
            long soLuongSanPhamNgay = chiTietDonHangRepository.sumSoLuongByDonHangThoiGianDatHangBetween(startOfYesterday, endOfYesterday);

            tkNgay.setTongDoanhThu((int) doanhThuNgay);
            tkNgay.setSoDon((int) soDonNgay);
            tkNgay.setSoSanPham((int) soLuongSanPhamNgay);
            log.info("Đã cập nhật ThongKeNgay cho {}: Doanh thu={}, Số đơn={}, Số sản phẩm={}", homQua, doanhThuNgay, soDonNgay, soLuongSanPhamNgay);
        });

        // --- Tổng hợp lại cho ThongKeThang ---
        YearMonth thangCuaHomQua = YearMonth.from(homQua);
        thongKeThangRepository.findByNamAndThang(thangCuaHomQua.getYear(), thangCuaHomQua.getMonthValue()).ifPresent(tkThang -> {
            LocalDateTime startOfMonth = thangCuaHomQua.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = thangCuaHomQua.atEndOfMonth().atTime(LocalTime.MAX);

            long doanhThuThang = donHangRepository.sumRevenueByStatusAndTimeRange(TrangThaiDonHang.DA_GIAO, startOfMonth, endOfMonth);
            long soDonHangThang = donHangRepository.countByThoiGianDatHangBetween(startOfMonth, endOfMonth);
            long soSanPhamThang = chiTietDonHangRepository.sumSoLuongByDonHangThoiGianDatHangBetween(startOfMonth, endOfMonth);

            Map<TrangThaiDonHang, Long> soDonTheoTrangThai = donHangRepository.findSoLuongTheoTrangThaiInTimeRange(startOfMonth, endOfMonth)
                    .stream().collect(Collectors.toMap(SoLuongDonHangTheoTrangThai::getTrangThai, SoLuongDonHangTheoTrangThai::getSoLuong));

            tkThang.setDoanhThu((int) doanhThuThang);
            tkThang.setSoDonHang((int) soDonHangThang);
            tkThang.setSoSanPham((int) soSanPhamThang);
            tkThang.setDonChoXacNhan(soDonTheoTrangThai.getOrDefault(TrangThaiDonHang.CHO_XAC_NHAN, 0L).intValue());
            tkThang.setDonChoXuLi(soDonTheoTrangThai.getOrDefault(TrangThaiDonHang.CHO_XU_LY, 0L).intValue());
            tkThang.setDonDangGiao(soDonTheoTrangThai.getOrDefault(TrangThaiDonHang.DANG_GIAO, 0L).intValue());
            tkThang.setDonDaGiao(soDonTheoTrangThai.getOrDefault(TrangThaiDonHang.DA_GIAO, 0L).intValue());
            tkThang.setDaDaHuy(soDonTheoTrangThai.getOrDefault(TrangThaiDonHang.DA_HUY, 0L).intValue());

            if (soDonHangThang > 0) {
                tkThang.setTrungBinhMoiDon(BigDecimal.valueOf(doanhThuThang).divide(BigDecimal.valueOf(soDonHangThang), 3, RoundingMode.HALF_UP));
            } else {
                tkThang.setTrungBinhMoiDon(BigDecimal.ZERO);
            }

            // Tính toán tăng trưởng so với tháng trước
            YearMonth thangTruoc = thangCuaHomQua.minusMonths(1);
            thongKeThangRepository.findByNamAndThang(thangTruoc.getYear(), thangTruoc.getMonthValue()).ifPresent(tkThangTruoc -> {
                tkThang.setTangTruongDoanhThu(tinhPhanTramTangTruong(tkThang.getDoanhThu(), tkThangTruoc.getDoanhThu()));
                tkThang.setTangTruongDonHang(tinhPhanTramTangTruong(tkThang.getSoDonHang(), tkThangTruoc.getSoDonHang()));
            });

            log.info("Đã tổng hợp lại ThongKeThang cho tháng {}/{}.", thangCuaHomQua.getMonthValue(), thangCuaHomQua.getYear());
        });

        log.info("Hoàn tất tác vụ tổng hợp dữ liệu thống kê.");
    }

    private Double tinhPhanTramTangTruong(long giaTriHienTai, long giaTriTruocDo) {
        if (giaTriTruocDo == 0) {
            // Nếu giá trị trước đó là 0, chỉ có 2 trường hợp:
            // 1. Tăng từ 0 lên > 0: Coi là tăng 100%
            // 2. Vẫn là 0: Tăng trưởng 0%
            return giaTriHienTai > 0 ? 100.0 : 0.0;
        }
        double percentage = ((double) (giaTriHienTai - giaTriTruocDo) / giaTriTruocDo) * 100.0;
        return BigDecimal.valueOf(percentage).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Tác vụ này chạy vào 01:00 mỗi ngày để cập nhật lại các sản phẩm bán chạy.
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?") // Chạy vào 01:00:00 mỗi ngày
    @Transactional
    public void updateBestSellingProducts() {
        log.info("Bắt đầu tác vụ cập nhật sản phẩm bán chạy...");

        // 1. Reset tất cả các sản phẩm đang có cờ 'banChay' về false
        sanPhamRepository.resetAllBanChayFlags();
        log.info("Đã reset cờ 'Bán chạy' cho tất cả sản phẩm.");

        // 2. Tìm ra top N sản phẩm bán chạy nhất trong M ngày qua
        LocalDateTime startTime = LocalDate.now().minusDays(LOOKBACK_DAYS).atStartOfDay();
        Pageable topN = PageRequest.of(0, TOP_N_PRODUCTS);
        List<Integer> topProductIds = sanPhamRepository.findTopSellingProductIds(TrangThaiDonHang.DA_GIAO, startTime, topN);

        if (topProductIds.isEmpty()) {
            log.info("Không tìm thấy sản phẩm bán chạy nào trong {} ngày qua. Hoàn tất tác vụ.", LOOKBACK_DAYS);
            return;
        }

        // 3. Đặt cờ 'banChay' = true cho các sản phẩm này
        sanPhamRepository.setBanChayForProductIds(topProductIds);
        log.info("Đã cập nhật cờ 'Bán chạy' cho {} sản phẩm. IDs: {}", topProductIds.size(), topProductIds);
        log.info("Hoàn tất tác vụ cập nhật sản phẩm bán chạy.");
    }
}