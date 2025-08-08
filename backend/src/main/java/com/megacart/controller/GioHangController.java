package com.megacart.controller;

import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.GioHangResponse;
import com.megacart.dto.response.ThongTinThanhToanResponse;
import com.megacart.model.TaiKhoan;
import com.megacart.service.GioHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<GioHangResponse> themVaoGioHang(
            @Valid @RequestBody ThemVaoGioHangRequest request,
            @AuthenticationPrincipal TaiKhoan taiKhoan) {
        GioHangResponse gioHangResponse = gioHangService.themVaoGioHang(request, taiKhoan);
        return ResponseEntity.ok(gioHangResponse);
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
}