package com.megacart.service.impl;

import com.megacart.dto.projection.DoanhThuTheoNgay;
import com.megacart.dto.projection.DoanhThuTheoThang;
import com.megacart.dto.projection.ChiTietSanPhamBanChayProjection;
import com.megacart.dto.projection.SoLuongDonHangTheoNgay;
import com.megacart.dto.projection.SanPhamBanChayProjection;
import com.megacart.dto.projection.SoLuongDaBanProjection;
import com.megacart.dto.projection.SoLuongDonHangTheoTrangThai;
import com.megacart.dto.projection.SoLuongDonHangTheoThang;
import com.megacart.dto.projection.TonKhoProjection;
import com.megacart.dto.request.CapNhatMucTieuDoanhThuRequest;
import com.megacart.dto.response.BieuDoDuongResponse;
import com.megacart.dto.response.DonHangGanDayResponse;
import com.megacart.dto.response.BieuDoTronResponse;
import com.megacart.dto.response.ChiTietDoanhThuThangResponse;
import com.megacart.dto.response.ChiTietSanPhamBanChayResponse;
import com.megacart.dto.response.ChiTietDonHangThangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.MucTieuDoanhThuResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamBanChayResponse;
import com.megacart.dto.response.SanPhamTonKhoResponse;
import com.megacart.dto.response.ThongKeTongQuanResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.model.DonHang;
import com.megacart.model.ThongKeThang;
import com.megacart.repository.ChiTietDonHangRepository;
import com.megacart.repository.DonHangRepository;
import com.megacart.repository.KhoRepository;
import com.megacart.repository.ThongKeThangRepository;
import com.megacart.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {

    private final DonHangRepository donHangRepository;
    private final ThongKeThangRepository thongKeThangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhoRepository khoRepository;

    @Override
    @Transactional(readOnly = true)
    public ThongKeTongQuanResponse getThongKeTongQuan() {
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        // 1. Định nghĩa các khoảng thời gian
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

        LocalDateTime startOfThisMonth = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfThisMonth = thisMonth.atEndOfMonth().atTime(LocalTime.MAX);

        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // 2. Lấy dữ liệu doanh thu (chỉ tính đơn hàng DA_GIAO)
        long doanhThuHomNay = donHangRepository.sumRevenueByStatusAndTimeRange(TrangThaiDonHang.DA_GIAO, startOfToday, endOfToday);
        long doanhThuThangNay = donHangRepository.sumRevenueByStatusAndTimeRange(TrangThaiDonHang.DA_GIAO, startOfThisMonth, endOfThisMonth);
        long doanhThuThangTruoc = donHangRepository.sumRevenueByStatusAndTimeRange(TrangThaiDonHang.DA_GIAO, startOfLastMonth, endOfLastMonth);

        // 3. Lấy dữ liệu số lượng đơn hàng (tính tất cả đơn hàng được tạo)
        long donHangHomNay = donHangRepository.countByThoiGianDatHangBetween(startOfToday, endOfToday);
        long donHangThangNay = donHangRepository.countByThoiGianDatHangBetween(startOfThisMonth, endOfThisMonth);
        long donHangThangTruoc = donHangRepository.countByThoiGianDatHangBetween(startOfLastMonth, endOfLastMonth);
        long soDonDangGiao = donHangRepository.countByTrangThai(TrangThaiDonHang.DANG_GIAO);

        // 4. Tính toán tăng trưởng
        Double tangTruongDoanhThu = tinhPhanTramTangTruong(doanhThuThangNay, doanhThuThangTruoc);
        Double tangTruongDonHang = tinhPhanTramTangTruong(donHangThangNay, donHangThangTruoc);

        // 5. Xây dựng đối tượng response
        ThongKeTongQuanResponse.DoanhThu doanhThuResponse = ThongKeTongQuanResponse.DoanhThu.builder()
                .homNay(doanhThuHomNay)
                .thangNay(doanhThuThangNay)
                .tangTruongSoVoiThangTruoc(tangTruongDoanhThu)
                .build();

        ThongKeTongQuanResponse.DonHang donHangResponse = ThongKeTongQuanResponse.DonHang.builder()
                .homNay(donHangHomNay)
                .thangNay(donHangThangNay)
                .tangTruongSoVoiThangTruoc(tangTruongDonHang)
                .soDonDangGiao(soDonDangGiao)
                .build();

        return ThongKeTongQuanResponse.builder()
                .doanhThu(doanhThuResponse)
                .donHang(donHangResponse)
                .build();
    }

    /**
     * Tính toán phần trăm tăng trưởng so với kỳ trước.
     * @param giaTriHienTai Giá trị của kỳ hiện tại.
     * @param giaTriTruocDo Giá trị của kỳ trước.
     * @return Phần trăm tăng trưởng, hoặc null nếu không thể tính toán (kỳ trước = 0).
     */
    private Double tinhPhanTramTangTruong(long giaTriHienTai, long giaTriTruocDo) {
        if (giaTriTruocDo == 0) {
            // Nếu tháng trước không có doanh thu/đơn hàng, không thể tính tăng trưởng.
            // Trả về null để frontend có thể xử lý hiển thị phù hợp (ví dụ: "N/A" hoặc một icon mũi tên lên).
            return giaTriHienTai > 0 ? 100.0 : null;
        }
        // Công thức: ((Mới - Cũ) / Cũ) * 100
        double percentage = ((double) (giaTriHienTai - giaTriTruocDo) / giaTriTruocDo) * 100.0;
        // Làm tròn đến 1 chữ số thập phân
        return BigDecimal.valueOf(percentage).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    @Transactional(readOnly = true)
    public BieuDoDuongResponse getDoanhThuTheoNgay(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Khoảng thời gian (period) phải là số dương.");
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period - 1);

        // 1. Lấy dữ liệu thô từ CSDL
        List<DoanhThuTheoNgay> doanhThuData = donHangRepository.findDoanhThuTheoNgay(
                TrangThaiDonHang.DA_GIAO,
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        // 2. Chuyển đổi dữ liệu thô sang Map để tra cứu nhanh (Key: Ngày, Value: Doanh thu)
        Map<LocalDate, Long> doanhThuMap = doanhThuData.stream()
                .collect(Collectors.toMap(DoanhThuTheoNgay::getNgay, DoanhThuTheoNgay::getTongDoanhThu));

        // 3. Tạo danh sách đầy đủ các ngày trong khoảng thời gian và điền dữ liệu
        // Điều này đảm bảo những ngày không có doanh thu sẽ có giá trị là 0.
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 0; i < period; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            labels.add(currentDate.format(formatter));
            data.add(doanhThuMap.getOrDefault(currentDate, 0L));
        }

        return BieuDoDuongResponse.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BieuDoDuongResponse getDonHangTheoThang() {
        final int MONTH_PERIOD = 6;
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(MONTH_PERIOD - 1);

        // 1. Xác định khoảng thời gian
        LocalDateTime startTime = startMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // 2. Lấy dữ liệu thô từ CSDL cho 6 tháng gần nhất
        List<SoLuongDonHangTheoThang> donHangData = donHangRepository.findSoLuongDonHangTheoThangInRange(startTime, endTime);

        // 3. Chuyển đổi dữ liệu thô sang Map để tra cứu nhanh (Key: YearMonth, Value: Số lượng)
        // Sử dụng vòng lặp for-each để tránh lỗi suy luận kiểu phức tạp của trình biên dịch.
        Map<YearMonth, Long> donHangMap = new java.util.LinkedHashMap<>();
        for (SoLuongDonHangTheoThang d : donHangData) {
            donHangMap.put(YearMonth.of(d.getNam(), d.getThang()), d.getSoLuong());
        }

        // 4. Tạo danh sách 6 tháng gần nhất và điền dữ liệu
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        for (int i = 0; i < MONTH_PERIOD; i++) {
            YearMonth month = startMonth.plusMonths(i);
            labels.add(month.format(formatter));
            data.add(donHangMap.getOrDefault(month, 0L));
        }
        return BieuDoDuongResponse.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MucTieuDoanhThuResponse getMucTieuDoanhThuThangNay() {
        YearMonth thisMonth = YearMonth.now();

        // 1. Lấy mục tiêu doanh thu của tháng này từ CSDL
        long mucTieu = thongKeThangRepository.findByNamAndThang(thisMonth.getYear(), thisMonth.getMonthValue())
                .map(ThongKeThang::getMucTieuDoanhThu) // Lấy giá trị mục tiêu
                .map(Integer::longValue) // Chuyển từ Integer sang long
                .orElse(0L); // Mặc định là 0 nếu chưa được thiết lập

        // 2. Lấy doanh thu thực tế của tháng này từ CSDL
        LocalDateTime startOfThisMonth = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfThisMonth = thisMonth.atEndOfMonth().atTime(LocalTime.MAX);
        long thucTe = donHangRepository.sumRevenueByStatusAndTimeRange(TrangThaiDonHang.DA_GIAO, startOfThisMonth, endOfThisMonth);

        return MucTieuDoanhThuResponse.builder()
                .mucTieu(mucTieu)
                .thucTe(thucTe)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse capNhatMucTieuDoanhThuThangNay(CapNhatMucTieuDoanhThuRequest request) {
        YearMonth thisMonth = YearMonth.now();
        int nam = thisMonth.getYear();
        int thang = thisMonth.getMonthValue();

        // Tìm bản ghi thống kê của tháng này, hoặc tạo mới nếu chưa có
        ThongKeThang thongKeThang = thongKeThangRepository.findByNamAndThang(nam, thang)
                .orElseGet(() -> ThongKeThang.builder()
                        .nam(nam)
                        .thang(thang)
                        .doanhThu(0)
                        .tangTruongDoanhThu(0.0)
                        .soDonHang(0)
                        .tangTruongDonHang(0.0)
                        .soSanPham(0)
                        .trungBinhMoiDon(BigDecimal.ZERO)
                        .donChoXacNhan(0).donChoXuLi(0).donDangGiao(0).donDaGiao(0).daDaHuy(0)
                        .build());

        thongKeThang.setMucTieuDoanhThu(request.getMucTieuMoi().intValue());
        thongKeThangRepository.save(thongKeThang);

        return new MessageResponse("Cập nhật mục tiêu doanh thu tháng " + thisMonth.getMonthValue() + "/" + thisMonth.getYear() + " thành công.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BieuDoTronResponse> getTiLeDonHang() {
        // 1. Lấy dữ liệu thô từ CSDL
        List<SoLuongDonHangTheoTrangThai> stats = donHangRepository.findSoLuongTheoTrangThai();
        Map<TrangThaiDonHang, Long> statsMap = stats.stream()
                .collect(Collectors.toMap(SoLuongDonHangTheoTrangThai::getTrangThai, SoLuongDonHangTheoTrangThai::getSoLuong));

        // 2. Định nghĩa thứ tự hiển thị mong muốn, khớp với màu sắc của frontend
        List<TrangThaiDonHang> orderedStatuses = List.of(
                TrangThaiDonHang.CHO_XAC_NHAN,
                TrangThaiDonHang.CHO_XU_LY,
                TrangThaiDonHang.DANG_GIAO,
                TrangThaiDonHang.DA_GIAO,
                TrangThaiDonHang.DA_HUY
        );

        // 3. Tạo response theo đúng thứ tự, điền 0 cho các trạng thái không có đơn hàng
        return orderedStatuses.stream()
                .map(item -> BieuDoTronResponse.builder()
                        .label(item.getTenHienThi())
                        .value(statsMap.getOrDefault(item, 0L))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamBanChayResponse> getSanPhamBanChay(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn (limit) phải là số dương.");
        }
        Pageable pageable = PageRequest.of(0, limit);
        List<SanPhamBanChayProjection> topProducts = chiTietDonHangRepository.findTopBanChay(TrangThaiDonHang.DA_GIAO, pageable);

        return topProducts.stream()
                .map(p -> new SanPhamBanChayResponse(p.getTenSanPham(), p.getSoLuongDaBan()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamTonKhoResponse> getSanPhamTonKhoCao(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn (limit) phải là số dương.");
        }
        Pageable pageable = PageRequest.of(0, limit);

        // 1. Lấy danh sách sản phẩm có tồn kho cao nhất
        List<TonKhoProjection> tonKhoList = khoRepository.findTopBySoLuongDesc(pageable);
        if (tonKhoList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Lấy mã của các sản phẩm này
        List<Integer> maSanPhams = tonKhoList.stream()
                .map(TonKhoProjection::getMaSanPham)
                .collect(Collectors.toList());

        // 3. Lấy tổng số lượng đã bán cho các sản phẩm đó (chỉ tính đơn đã giao)
        Map<Integer, Long> soLuongDaBanMap = chiTietDonHangRepository
                .findSoLuongDaBanBySanPhamIds(TrangThaiDonHang.DA_GIAO, maSanPhams)
                .stream()
                .collect(Collectors.toMap(SoLuongDaBanProjection::getMaSanPham, SoLuongDaBanProjection::getSoLuongDaBan));

        // 4. Kết hợp hai nguồn dữ liệu
        return tonKhoList.stream()
                .map(tonKho -> SanPhamTonKhoResponse.builder()
                        .tenSanPham(tonKho.getTenSanPham())
                        .soLuongTon(tonKho.getSoLuongTon())
                        .soLuongDaBan(soLuongDaBanMap.getOrDefault(tonKho.getMaSanPham(), 0L))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDoanhThuThangResponse> getChiTietDoanhThuThang() {
        final int MONTH_PERIOD = 12;
        YearMonth currentMonth = YearMonth.now();

        // 1. Lấy dữ liệu thô của tối đa 12 tháng gần nhất có dữ liệu
        Pageable top12 = PageRequest.of(0, MONTH_PERIOD);
        List<ThongKeThang> thongKeData = thongKeThangRepository.findByOrderByNamDescThangDesc(top12).getContent();

        // 2. Chuyển đổi dữ liệu thô sang Map để tra cứu nhanh (Key: YearMonth, Value: ThongKeThang)
        Map<YearMonth, ThongKeThang> thongKeMap = thongKeData.stream()
                .collect(Collectors.toMap(
                        tk -> YearMonth.of(tk.getNam(), tk.getThang()),
                        tk -> tk
                ));

        // 3. Tạo danh sách 12 tháng gần nhất và điền dữ liệu (tháng mới nhất lên đầu)
        List<ChiTietDoanhThuThangResponse> result = new ArrayList<>();
        for (int i = 0; i < MONTH_PERIOD; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            ThongKeThang thongKe = thongKeMap.get(month);

            result.add(thongKe != null ? mapToChiTietDoanhThuThangResponse(thongKe)
                    : createDefaultChiTietDoanhThuThangResponse(month));
        }
        return result;
    }

    private ChiTietDoanhThuThangResponse mapToChiTietDoanhThuThangResponse(ThongKeThang thongKe) {
        Integer mucTieu = thongKe.getMucTieuDoanhThu();
        Integer doanhThu = thongKe.getDoanhThu();
        Double tiLeDatMucTieu = null;

        if (mucTieu != null && mucTieu > 0) {
            double tiLe = ((double) doanhThu / mucTieu) * 100.0;
            tiLeDatMucTieu = BigDecimal.valueOf(tiLe).setScale(1, RoundingMode.HALF_UP).doubleValue();
        }

        return ChiTietDoanhThuThangResponse.builder()
                .thang(thongKe.getThang() + "/" + thongKe.getNam())
                .mucTieu(mucTieu)
                .doanhThu(doanhThu)
                .tiLeDatMucTieu(tiLeDatMucTieu)
                .tangTruong(thongKe.getTangTruongDoanhThu())
                .trungBinhMoiDon(thongKe.getTrungBinhMoiDon())
                .build();
    }

    private ChiTietDoanhThuThangResponse createDefaultChiTietDoanhThuThangResponse(YearMonth month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/yyyy");
        return ChiTietDoanhThuThangResponse.builder()
                .thang(month.format(formatter))
                .mucTieu(0)
                .doanhThu(0)
                .tiLeDatMucTieu(0.0) // Trả về 0.0 thay vì null
                .tangTruong(0.0) // Trả về 0.0 thay vì null
                .trungBinhMoiDon(BigDecimal.ZERO)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public BieuDoDuongResponse getDoanhThuTheoThang() {
        final int MONTH_PERIOD = 6;
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(MONTH_PERIOD - 1);

        // 1. Xác định khoảng thời gian
        LocalDateTime startTime = startMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // 2. Lấy dữ liệu thô từ CSDL cho 6 tháng gần nhất (chỉ tính đơn đã giao)
        List<DoanhThuTheoThang> doanhThuData = donHangRepository.findDoanhThuTheoThangInRange(TrangThaiDonHang.DA_GIAO, startTime, endTime);

        // 3. Chuyển đổi dữ liệu thô sang Map để tra cứu nhanh (Key: YearMonth, Value: Doanh thu)
        // Sử dụng vòng lặp for-each để tránh lỗi suy luận kiểu phức tạp của trình biên dịch.
        Map<YearMonth, Long> doanhThuMap = new java.util.LinkedHashMap<>();
        for (DoanhThuTheoThang d : doanhThuData) {
            doanhThuMap.put(YearMonth.of(d.getNam(), d.getThang()), d.getTongDoanhThu());
        }

        // 4. Tạo danh sách 6 tháng gần nhất và điền dữ liệu
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        for (int i = 0; i < MONTH_PERIOD; i++) {
            YearMonth month = startMonth.plusMonths(i);
            labels.add(month.format(formatter));
            data.add(doanhThuMap.getOrDefault(month, 0L));
        }
        return BieuDoDuongResponse.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BieuDoDuongResponse getDonHangTheoNgay(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Khoảng thời gian (period) phải là số dương.");
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period - 1);

        // 1. Lấy dữ liệu thô từ CSDL
        List<SoLuongDonHangTheoNgay> donHangData = donHangRepository.findSoLuongDonHangTheoNgay(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        // 2. Chuyển đổi dữ liệu thô sang Map để tra cứu nhanh (Key: Ngày, Value: Số lượng)
        Map<LocalDate, Long> donHangMap = donHangData.stream()
                .collect(Collectors.toMap(SoLuongDonHangTheoNgay::getNgay, SoLuongDonHangTheoNgay::getSoLuong));

        // 3. Tạo danh sách đầy đủ các ngày trong khoảng thời gian và điền dữ liệu
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 0; i < period; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            labels.add(currentDate.format(formatter));
            data.add(donHangMap.getOrDefault(currentDate, 0L));
        }

        return BieuDoDuongResponse.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHangThangResponse> getChiTietDonHangThang() {
        final int MONTH_PERIOD = 12;
        YearMonth currentMonth = YearMonth.now();

        // 1. Lấy dữ liệu thô của tối đa 12 tháng gần nhất có dữ liệu
        Pageable top12 = PageRequest.of(0, MONTH_PERIOD);
        List<ThongKeThang> thongKeData = thongKeThangRepository.findByOrderByNamDescThangDesc(top12).getContent();

        // 2. Chuyển đổi dữ liệu thô sang Map để tra cứu nhanh (Key: YearMonth, Value: ThongKeThang)
        Map<YearMonth, ThongKeThang> thongKeMap = thongKeData.stream()
                .collect(Collectors.toMap(
                        tk -> YearMonth.of(tk.getNam(), tk.getThang()),
                        tk -> tk
                ));

        // 3. Tạo danh sách 12 tháng gần nhất và điền dữ liệu (tháng mới nhất lên đầu)
        List<ChiTietDonHangThangResponse> result = new ArrayList<>();
        for (int i = 0; i < MONTH_PERIOD; i++) {
            YearMonth month = currentMonth.minusMonths(i);
            ThongKeThang thongKe = thongKeMap.get(month);

            result.add(thongKe != null ? mapToChiTietDonHangThangResponse(thongKe)
                    : createDefaultChiTietDonHangThangResponse(month));
        }
        return result;
    }

    private ChiTietDonHangThangResponse mapToChiTietDonHangThangResponse(ThongKeThang thongKe) {
        Integer soDonHang = thongKe.getSoDonHang();
        ChiTietDonHangThangResponse.ThongKeTiLe donGiaoThanhCong = null;
        ChiTietDonHangThangResponse.ThongKeTiLe donBiHuy = null;

        if (soDonHang != null && soDonHang > 0) {
            double successRate = ((double) thongKe.getDonDaGiao() / soDonHang) * 100.0;
            donGiaoThanhCong = ChiTietDonHangThangResponse.ThongKeTiLe.builder()
                    .soLuong(thongKe.getDonDaGiao())
                    .phanTram(BigDecimal.valueOf(successRate).setScale(1, RoundingMode.HALF_UP).doubleValue())
                    .build();

            double cancelledRate = ((double) thongKe.getDaDaHuy() / soDonHang) * 100.0;
            donBiHuy = ChiTietDonHangThangResponse.ThongKeTiLe.builder()
                    .soLuong(thongKe.getDaDaHuy())
                    .phanTram(BigDecimal.valueOf(cancelledRate).setScale(1, RoundingMode.HALF_UP).doubleValue())
                    .build();
        }

        return ChiTietDonHangThangResponse.builder()
                .thang(thongKe.getThang() + "/" + thongKe.getNam())
                .soDonHang(soDonHang)
                .tangTruong(thongKe.getTangTruongDonHang())
                .trungBinhMoiDon(thongKe.getTrungBinhMoiDon())
                .donGiaoThanhCong(donGiaoThanhCong)
                .donBiHuy(donBiHuy)
                .build();
    }

    private ChiTietDonHangThangResponse createDefaultChiTietDonHangThangResponse(YearMonth month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/yyyy");
        return ChiTietDonHangThangResponse.builder()
                .thang(month.format(formatter))
                .soDonHang(0)
                .tangTruong(0.0) // Trả về 0.0 thay vì null
                .trungBinhMoiDon(BigDecimal.ZERO)
                // Khởi tạo các đối tượng lồng nhau với giá trị mặc định
                .donGiaoThanhCong(ChiTietDonHangThangResponse.ThongKeTiLe.builder()
                        .soLuong(0)
                        .phanTram(0.0).build())
                .donBiHuy(ChiTietDonHangThangResponse.ThongKeTiLe.builder()
                        .soLuong(0)
                        .phanTram(0.0).build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ChiTietSanPhamBanChayResponse> getChiTietSanPhamBanChay(Pageable pageable) {
        // 1. Lấy dữ liệu đã được tổng hợp sẵn từ CSDL, có phân trang
        Page<ChiTietSanPhamBanChayProjection> banChayPage = chiTietDonHangRepository.findChiTietBanChay(TrangThaiDonHang.DA_GIAO, pageable);

        // 2. Chuyển đổi sang DTO, tính toán các chỉ số còn lại
        List<ChiTietSanPhamBanChayResponse> content = banChayPage.getContent().stream()
                .map(this::mapToChiTietSanPhamBanChayResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                banChayPage.getNumber(),
                banChayPage.getSize(),
                banChayPage.getTotalElements(),
                banChayPage.getTotalPages(),
                null
        );
    }

    private ChiTietSanPhamBanChayResponse mapToChiTietSanPhamBanChayResponse(ChiTietSanPhamBanChayProjection projection) {
        Long soLuongBanRa = projection.getSoLuongBanRa();
        Long soDonDat = projection.getSoDonDat();
        Double soLuongTrungBinhMoiDon = null;

        if (soDonDat != null && soDonDat > 0 && soLuongBanRa != null) {
            double avg = (double) soLuongBanRa / soDonDat;
            soLuongTrungBinhMoiDon = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue();
        }

        return ChiTietSanPhamBanChayResponse.builder()
                .maSanPham(projection.getMaSanPham())
                .tenSanPham(projection.getTenSanPham())
                .soLuongBanRa(soLuongBanRa)
                .soDonDat(soDonDat)
                .soLuongTrungBinhMoiDon(soLuongTrungBinhMoiDon)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHangGanDayResponse> getDonHangGanDay(int limit) {
        // 1. Tạo Pageable để lấy 'limit' bản ghi đầu tiên, sắp xếp theo thời gian đặt hàng giảm dần
        Pageable pageable = PageRequest.of(0, limit);

        // 2. Gọi phương thức repository đã được tối ưu với @EntityGraph
        List<DonHang> donHangs = donHangRepository.findByOrderByThoiGianDatHangDesc(pageable).getContent();

        // 3. Ánh xạ sang DTO
        return donHangs.stream()
                .map(this::mapToDonHangGanDayResponse)
                .collect(Collectors.toList());
    }

    private DonHangGanDayResponse mapToDonHangGanDayResponse(DonHang donHang) {
        // Tính tổng tiền từ các chi tiết đơn hàng đã được fetch sẵn
        long tongTien = donHang.getChiTietDonHangs().stream()
                .mapToLong(ct -> (long) ct.getDonGia() * ct.getSoLuong())
                .sum();

        return DonHangGanDayResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .tenKhachHang(donHang.getTenKhachHang())
                .thoiGianDatHang(donHang.getThoiGianDatHang())
                .trangThai(donHang.getTrangThai())
                .tongTien(tongTien)
                .build();
    }
}
