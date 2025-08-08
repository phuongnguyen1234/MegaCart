package com.megacart.controller;

import com.megacart.dto.request.CapNhatThongTinCoBanRequest;
import com.megacart.dto.request.KhoiTaoDoiEmailRequest;
import com.megacart.dto.request.XacNhanDoiEmailRequest;
import com.megacart.dto.response.ThongTinKhachHangResponse;
import com.megacart.model.TaiKhoan;
import com.megacart.service.KhachHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('KHACH_HANG')") // Chỉ khách hàng mới có quyền truy cập các API này
public class KhachHangController {

    private final KhachHangService khachHangService;

    /**
     * Lấy thông tin tài khoản của khách hàng đang đăng nhập.
     * @param taiKhoan Đối tượng TaiKhoan được Spring Security tự động inject vào.
     * @return Thông tin chi tiết của khách hàng.
     */
    @GetMapping("/thong-tin")
    public ResponseEntity<ThongTinKhachHangResponse> getThongTin(@AuthenticationPrincipal TaiKhoan taiKhoan) {
        ThongTinKhachHangResponse response = khachHangService.getThongTinKhachHang(taiKhoan.getMaTaiKhoan());
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật thông tin cơ bản (tên, sđt, địa chỉ) không cần OTP.
     * @param taiKhoan Đối tượng TaiKhoan của người dùng đang đăng nhập.
     * @param request Dữ liệu cập nhật.
     * @return Thông tin đã được cập nhật.
     */
    @PutMapping("/ho-so")
    public ResponseEntity<ThongTinKhachHangResponse> capNhatThongTinCoBan(@AuthenticationPrincipal TaiKhoan taiKhoan, @Valid @RequestBody CapNhatThongTinCoBanRequest request) {
        ThongTinKhachHangResponse response = khachHangService.capNhatThongTinCoBan(taiKhoan.getMaTaiKhoan(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * Bước 1: Gửi yêu cầu thay đổi email. Hệ thống sẽ gửi OTP đến email CŨ để xác thực.
     */
    @PostMapping("/email/yeu-cau")
    public ResponseEntity<Void> khoiTaoDoiEmail(@AuthenticationPrincipal TaiKhoan taiKhoan, @Valid @RequestBody KhoiTaoDoiEmailRequest request) {
        khachHangService.khoiTaoDoiEmail(taiKhoan.getMaTaiKhoan(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * Bước 2: Gửi OTP để xác nhận và hoàn tất việc thay đổi email.
     */
    @PostMapping("/email/xac-nhan")
    public ResponseEntity<ThongTinKhachHangResponse> xacNhanDoiEmail(@AuthenticationPrincipal TaiKhoan taiKhoan, @Valid @RequestBody XacNhanDoiEmailRequest request) {
        ThongTinKhachHangResponse response = khachHangService.xacNhanDoiEmail(taiKhoan.getMaTaiKhoan(), request.getOtp());
        return ResponseEntity.ok(response);
    }
}