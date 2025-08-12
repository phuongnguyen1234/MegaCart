package com.megacart.controller;

import com.megacart.dto.request.CapNhatSoLuongRequest;
import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.GioHangResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.ThemVaoGioHangResponse;
import com.megacart.dto.response.ThongTinThanhToanResponse;
import com.megacart.dto.response.XoaKhoiGioHangResponse;
import com.megacart.model.TaiKhoan;
import com.megacart.service.GioHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gio-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('KHACH_HANG')") // Chỉ khách hàng mới có quyền truy cập giỏ hàng
public class GioHangController {

    private final GioHangService gioHangService;

    @PostMapping("/them")
    public ResponseEntity<ThemVaoGioHangResponse> themVaoGioHang(
            @Valid @RequestBody ThemVaoGioHangRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        ThemVaoGioHangResponse response = gioHangService.themVaoGioHang(request, taiKhoan);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy toàn bộ thông tin cần thiết cho trang giỏ hàng và thanh toán.
     * Bao gồm danh sách sản phẩm, tổng tiền, và thông tin giao hàng mặc định của khách.
     * @param taiKhoan Khách hàng đang đăng nhập.
     * @return Một đối tượng chứa tất cả thông tin cần thiết.
     */
    @GetMapping
    public ResponseEntity<ThongTinThanhToanResponse> getThongTinThanhToan(@AuthenticationPrincipal TaiKhoan taiKhoan) {
        ThongTinThanhToanResponse response = gioHangService.getThongTinThanhToan(taiKhoan);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cap-nhat/{maSanPham}") // PATCH /api/gio-hang/cap-nhat/{id} -> Cập nhật số lượng của một item
    public ResponseEntity<GioHangResponse> capNhatSoLuong(
            @PathVariable Integer maSanPham,
            @Valid @RequestBody CapNhatSoLuongRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan
    ) {
        GioHangResponse response = gioHangService.capNhatSoLuong(maSanPham, request, taiKhoan);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/xoa/{maSanPham}")
    public ResponseEntity<XoaKhoiGioHangResponse> xoaKhoiGioHang(
            @PathVariable Integer maSanPham,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        XoaKhoiGioHangResponse response = gioHangService.xoaKhoiGioHang(maSanPham, taiKhoan);
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa toàn bộ các sản phẩm trong giỏ hàng của người dùng hiện tại.
     */
    @DeleteMapping("/xoa-toan-bo")
    public ResponseEntity<MessageResponse> xoaToanBoGioHang(@AuthenticationPrincipal TaiKhoan taiKhoan) {
        gioHangService.xoaToanBoGioHang(taiKhoan);
        return ResponseEntity.ok(new MessageResponse("Đã xóa toàn bộ sản phẩm khỏi giỏ hàng."));
    }
}