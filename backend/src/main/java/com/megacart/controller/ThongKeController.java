package com.megacart.controller;

import com.megacart.dto.request.CapNhatMucTieuDoanhThuRequest;
import com.megacart.dto.response.BieuDoDuongResponse;
import com.megacart.dto.response.BieuDoTronResponse;
import com.megacart.dto.response.ChiTietSanPhamBanChayResponse;
import com.megacart.dto.response.ChiTietDonHangThangResponse;
import com.megacart.dto.response.ChiTietDoanhThuThangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.MucTieuDoanhThuResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamBanChayResponse;
import com.megacart.dto.response.SanPhamTonKhoResponse;
import com.megacart.dto.response.ThongKeTongQuanResponse;
import com.megacart.service.ThongKeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/thong-ke")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Chỉ Admin mới có quyền xem thống kê tổng quan
public class ThongKeController {

    private final ThongKeService thongKeService;

    @GetMapping("/tong-quan")
    public ResponseEntity<ThongKeTongQuanResponse> getThongKeTongQuan() {
        return ResponseEntity.ok(thongKeService.getThongKeTongQuan());
    }

    @GetMapping("/doanh-thu-theo-ngay")
    public ResponseEntity<BieuDoDuongResponse> getDoanhThuTheoNgay(
            // Mặc định là 7 ngày gần nhất nếu không có tham số
            @RequestParam(defaultValue = "7") int period) {
        return ResponseEntity.ok(thongKeService.getDoanhThuTheoNgay(period));
    }

    @GetMapping("/don-hang-theo-thang")
    public ResponseEntity<BieuDoDuongResponse> getDonHangTheoThang(
            // Tham số năm là bắt buộc
            @RequestParam int year) {
        return ResponseEntity.ok(thongKeService.getDonHangTheoThang(year));
    }

    @GetMapping("/muc-tieu-doanh-thu")
    public ResponseEntity<MucTieuDoanhThuResponse> getMucTieuDoanhThu() {
        return ResponseEntity.ok(thongKeService.getMucTieuDoanhThuThangNay());
    }

    @PutMapping("/muc-tieu-doanh-thu")
    public ResponseEntity<MessageResponse> capNhatMucTieuDoanhThu(
            @Valid @RequestBody CapNhatMucTieuDoanhThuRequest request) {
        return ResponseEntity.ok(thongKeService.capNhatMucTieuDoanhThuThangNay(request));
    }

    @GetMapping("/ti-le-don-hang")
    public ResponseEntity<List<BieuDoTronResponse>> getTiLeDonHang() {
        return ResponseEntity.ok(thongKeService.getTiLeDonHang());
    }

    @GetMapping("/san-pham-ban-chay")
    public ResponseEntity<List<SanPhamBanChayResponse>> getSanPhamBanChay(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(thongKeService.getSanPhamBanChay(limit));
    }

    @GetMapping("/san-pham-ton-kho-cao")
    public ResponseEntity<List<SanPhamTonKhoResponse>> getSanPhamTonKhoCao(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(thongKeService.getSanPhamTonKhoCao(limit));
    }

    @GetMapping("/chi-tiet-doanh-thu-thang")
    public ResponseEntity<PagedResponse<ChiTietDoanhThuThangResponse>> getChiTietDoanhThuThang(
            @RequestParam int year,
            @PageableDefault(size = 12, sort = "thang", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(thongKeService.getChiTietDoanhThuThang(year, pageable));
    }

    @GetMapping("/doanh-thu-theo-thang")
    public ResponseEntity<BieuDoDuongResponse> getDoanhThuTheoThang(
            // Tham số năm là bắt buộc
            @RequestParam int year) {
        return ResponseEntity.ok(thongKeService.getDoanhThuTheoThang(year));
    }

    @GetMapping("/don-hang-theo-ngay")
    public ResponseEntity<BieuDoDuongResponse> getDonHangTheoNgay(
            // Mặc định là 7 ngày gần nhất nếu không có tham số
            @RequestParam(defaultValue = "7") int period) {
        return ResponseEntity.ok(thongKeService.getDonHangTheoNgay(period));
    }

    @GetMapping("/chi-tiet-don-hang-thang")
    public ResponseEntity<PagedResponse<ChiTietDonHangThangResponse>> getChiTietDonHangThang(
            @RequestParam int year,
            @PageableDefault(size = 12, sort = "thang", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(thongKeService.getChiTietDonHangThang(year, pageable));
    }

    @GetMapping("/chi-tiet-san-pham-ban-chay")
    public ResponseEntity<PagedResponse<ChiTietSanPhamBanChayResponse>> getChiTietSanPhamBanChay(
            // Sắp xếp đã được xử lý trong query, không cần sort ở đây
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(thongKeService.getChiTietSanPhamBanChay(pageable));
    }
}
