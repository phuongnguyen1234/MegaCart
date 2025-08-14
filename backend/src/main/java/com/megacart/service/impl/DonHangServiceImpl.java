package com.megacart.service.impl;

import com.megacart.dto.request.HuyDonHangRequest;
import com.megacart.dto.response.ChiTietDonHangResponse;
import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;

import com.megacart.dto.response.LichSuDonHangResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.enumeration.TrangThaiXuLi;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.*;
import com.megacart.repository.*;
import com.megacart.service.DonHangService;
import com.megacart.utils.ImageUtils;
import com.megacart.utils.ThoiGianGiaoHangUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonHangServiceImpl implements DonHangService {

    private final SanPhamRepository sanPhamRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final KhachHangRepository khachHangRepository;

    

    @Override
    @Transactional(readOnly = true) // readOnly = true để tối ưu cho các query chỉ đọc
    public PagedResponse<LichSuDonHangResponse> getLichSuMuaHang(TaiKhoan taiKhoan, TrangThaiDonHang trangThai, String tuKhoa, LocalDate tuNgay, LocalDate denNgay, Pageable pageable) {
        Integer maKhachHang = taiKhoan.getMaTaiKhoan();

        // Chuyển đổi LocalDate sang LocalDateTime để truy vấn trong CSDL
        // tuNgay sẽ là 00:00:00 của ngày bắt đầu
        // denNgay sẽ là 23:59:59 của ngày kết thúc
        LocalDateTime startOfDay = (tuNgay != null) ? tuNgay.atStartOfDay() : null;
        LocalDateTime endOfDay = (denNgay != null) ? denNgay.atTime(LocalTime.MAX) : null;

        // Gọi phương thức repository duy nhất, truyền tất cả các tham số (một số có thể là null)
        Page<DonHang> donHangPage = donHangRepository.searchByCriteria(maKhachHang, trangThai, tuKhoa, startOfDay, endOfDay, pageable);
        // 2. Chuyển đổi từ List<DonHang> sang List<LichSuDonHangResponse>
        List<LichSuDonHangResponse> lichSuDonHangs = donHangPage.getContent().stream()
                .map(this::mapToLichSuDonHangResponse)
                .collect(Collectors.toList());

        // 3. Tạo và trả về PagedResponse
        return new PagedResponse<>(
                lichSuDonHangs,
                donHangPage.getNumber(),
                donHangPage.getSize(),
                donHangPage.getTotalElements(),
                donHangPage.getTotalPages(),
                null
        );
    }

    /**
     * Phương thức helper để chuyển đổi một DonHang entity sang LichSuDonHangResponse DTO.
     * @param donHang Đối tượng DonHang entity.
     * @return Đối tượng LichSuDonHangResponse DTO.
     */
    private LichSuDonHangResponse mapToLichSuDonHangResponse(DonHang donHang) {
        List<ChiTietDonHang> chiTietDonHangs = donHang.getChiTietDonHangs();

        if (chiTietDonHangs.isEmpty()) {
            // Trường hợp hiếm gặp: đơn hàng không có sản phẩm nào.
            // Trả về thông tin cơ bản của đơn hàng.
            return LichSuDonHangResponse.builder()
                    .maDonHang(donHang.getMaDonHang())
                    .trangThai(donHang.getTrangThai().getTenHienThi())
                    .thoiGianDatHang(donHang.getThoiGianDatHang())
                    .tongTien(0)
                    .tenSanPhamDauTien("Đơn hàng không có sản phẩm")
                    .build();
        }

        // Lấy thông tin từ sản phẩm đầu tiên trong danh sách để hiển thị
        ChiTietDonHang sanPhamDauTien = chiTietDonHangs.get(0);

        // Tính toán tổng tiền và tổng số lượng
        long tongTien = chiTietDonHangs.stream().mapToLong(ct -> (long) ct.getDonGia() * ct.getSoLuong()).sum();

        // Tính số lượng các loại sản phẩm khác
        Integer soLuongLoaiSanPhamKhac = null;
        int count = chiTietDonHangs.size() - 1;
        if (count > 0) {
            soLuongLoaiSanPhamKhac = count;
        }

        return LichSuDonHangResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .trangThai(donHang.getTrangThai().getTenHienThi())
                .thoiGianDatHang(donHang.getThoiGianDatHang())
                .tongTien(tongTien)
                .tenSanPhamDauTien(sanPhamDauTien.getTenSanPham())
                .anhMinhHoaDauTien(sanPhamDauTien.getAnhMinhHoa())
                .soLuongDauTien(sanPhamDauTien.getSoLuong())
                .soLuongLoaiSanPhamKhac(soLuongLoaiSanPhamKhac)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietDonHangResponse getChiTietDonHang(Integer maDonHang, TaiKhoan taiKhoan) {
        DonHang donHang = donHangRepository.findByMaDonHangAndKhachHang_MaKhachHang(maDonHang, taiKhoan.getMaTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này."));

        return mapToChiTietDonHangResponse(donHang);
    }

    /**
     * Phương thức helper để chuyển đổi một DonHang entity sang ChiTietDonHangResponse DTO.
     * @param donHang Đối tượng DonHang entity.
     * @return Đối tượng ChiTietDonHangResponse DTO.
     */
    private ChiTietDonHangResponse mapToChiTietDonHangResponse(DonHang donHang) {
        // Tính tổng tiền
        long tongTien = donHang.getChiTietDonHangs().stream()
                .mapToLong(ct -> (long) ct.getDonGia() * ct.getSoLuong())
                .sum();

        ChiTietDonHangResponse.ChiTietDonHangResponseBuilder responseBuilder = ChiTietDonHangResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .tenNguoiNhan(donHang.getTenKhachHang())
                .sdtNhanHang(donHang.getSdtNhanHang())
                .diaChiDatHang(donHang.getDiaChiNhanHang())
                .thoiGianDatHang(donHang.getThoiGianDatHang())
                .trangThai(donHang.getTrangThai().getTenHienThi())
                .hinhThucGiaoHang(donHang.getHinhThucNhanHang().getTenHienThi())
                .hinhThucThanhToan(donHang.getHinhThucThanhToan().getTenHienThi())
                .trangThaiThanhToan(donHang.getTrangThaiThanhToan().getTenHienThi())
                .tongTien(tongTien);

        List<ChiTietDonHangResponse.ItemResponse> items;
        // Thêm trường thông tin tùy theo trạng thái đơn hàng
        switch (donHang.getTrangThai()) {
            case CHO_XAC_NHAN:
                responseBuilder.duKienGiaoHang(donHang.getDuKienGiaoHang());
                responseBuilder.ghiChu(donHang.getGhiChu());
                // Kiểm tra lại tồn kho hiện tại của từng sản phẩm
                items = donHang.getChiTietDonHangs().stream().map(chiTiet -> {
                    boolean hetHang = chiTiet.getSanPham().getKho() == null || chiTiet.getSanPham().getKho().getSoLuong() < chiTiet.getSoLuong();
                    return ChiTietDonHangResponse.ItemResponse.builder()
                            .maSanPham(chiTiet.getSanPham().getMaSanPham())
                            .tenSanPham(chiTiet.getTenSanPham())
                            .anhMinhHoa(chiTiet.getAnhMinhHoa())
                            .donGia(chiTiet.getDonGia())
                            .soLuong(chiTiet.getSoLuong())
                            .trangThaiItem(hetHang ? "Hết hàng" : null)
                            .build();
                }).collect(Collectors.toList());
                break;
            case DA_GIAO:
                responseBuilder.thoiGianThanhToan(donHang.getThoiGianThanhToan());
                items = mapToStandardItemResponseList(donHang);
                break;
            case DA_HUY:
                responseBuilder.ghiChu(donHang.getGhiChu());
                items = mapToStandardItemResponseList(donHang);
                break;
            default:
                // Các trạng thái còn lại hiển thị ngày giao hàng dự kiến
                responseBuilder.duKienGiaoHang(donHang.getDuKienGiaoHang());
                items = mapToStandardItemResponseList(donHang);
                break;
        }

        return responseBuilder.items(items).build();
    }

    private List<ChiTietDonHangResponse.ItemResponse> mapToStandardItemResponseList(DonHang donHang) {
        return donHang.getChiTietDonHangs().stream()
                .map(chiTiet -> ChiTietDonHangResponse.ItemResponse.builder().maSanPham(chiTiet.getSanPham().getMaSanPham())
                        .tenSanPham(chiTiet.getTenSanPham()).anhMinhHoa(chiTiet.getAnhMinhHoa())
                        .donGia(chiTiet.getDonGia()).soLuong(chiTiet.getSoLuong()).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChiTietDonHangResponse huyDonHang(Integer maDonHang, HuyDonHangRequest request, TaiKhoan taiKhoan) {
        DonHang donHang = donHangRepository.findByMaDonHangAndKhachHang_MaKhachHang(maDonHang, taiKhoan.getMaTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền thực hiện thao tác này."));

        TrangThaiDonHang trangThaiHienTai = donHang.getTrangThai();

        // Chỉ cho phép hủy ở các trạng thái nhất định
        if (trangThaiHienTai != TrangThaiDonHang.CHO_XAC_NHAN &&
            trangThaiHienTai != TrangThaiDonHang.CHO_XU_LY &&
            trangThaiHienTai != TrangThaiDonHang.DANG_GIAO) {
            throw new IllegalArgumentException("Đơn hàng ở trạng thái '" + trangThaiHienTai + "' không thể hủy.");
        }

        // Hoàn trả lại số lượng hàng vào kho nếu đơn hàng đã được xử lý hoặc đang giao
        if (trangThaiHienTai == TrangThaiDonHang.CHO_XU_LY || trangThaiHienTai == TrangThaiDonHang.DANG_GIAO) {
            for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                Kho kho = chiTiet.getSanPham().getKho();
                if (kho != null) {
                    kho.setSoLuong(kho.getSoLuong() + chiTiet.getSoLuong());
                }
            }
        }

        // Cập nhật trạng thái đơn hàng
        donHang.setTrangThai(TrangThaiDonHang.DA_HUY);
        donHang.setGhiChu(request.getLyDo()); // Lưu lý do hủy

        DonHang savedDonHang = donHangRepository.save(donHang);
        return mapToChiTietDonHangResponse(savedDonHang);
    }

    @Override
    @Transactional
    public ChiTietDonHangResponse giaoPhanConLai(Integer maDonHang, TaiKhoan taiKhoan) {
        DonHang donHang = donHangRepository.findByMaDonHangAndKhachHang_MaKhachHang(maDonHang, taiKhoan.getMaTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền thực hiện thao tác này."));

        // Thao tác này chỉ hợp lệ khi đơn hàng ở trạng thái CHO_XAC_NHAN
        if (donHang.getTrangThai() != TrangThaiDonHang.CHO_XAC_NHAN) {
            throw new IllegalArgumentException("Chức năng này chỉ áp dụng cho đơn hàng đang chờ xác nhận.");
        }

        List<ChiTietDonHang> itemsToRemove = new ArrayList<>();
        List<ChiTietDonHang> itemsToKeep = new ArrayList<>();

        // Phân loại sản phẩm còn hàng và hết hàng
        for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
            boolean hetHang = chiTiet.getSanPham().getKho() == null || chiTiet.getSanPham().getKho().getSoLuong() < chiTiet.getSoLuong();
            if (hetHang) {
                itemsToRemove.add(chiTiet);
            } else {
                itemsToKeep.add(chiTiet);
            }
        }

        if (itemsToKeep.isEmpty()) {
            throw new IllegalArgumentException("Không còn sản phẩm nào để giao. Vui lòng hủy đơn hàng.");
        }

        // Xóa các sản phẩm hết hàng khỏi đơn hàng
        itemsToRemove.forEach(donHang::removeChiTietDonHang);

        // Trừ kho cho các sản phẩm còn lại và chuyển trạng thái đơn hàng
        for (ChiTietDonHang chiTiet : itemsToKeep) {
            Kho kho = chiTiet.getSanPham().getKho();
            kho.setSoLuong(kho.getSoLuong() - chiTiet.getSoLuong());
        }
        donHang.setTrangThai(TrangThaiDonHang.CHO_XU_LY); // Chuyển sang chờ xử lý để nhân viên đóng gói

        DonHang savedDonHang = donHangRepository.save(donHang);
        return mapToChiTietDonHangResponse(savedDonHang);
    }
}
