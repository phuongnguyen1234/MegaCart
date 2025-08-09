package com.megacart.controller;

import com.megacart.dto.request.CapNhatHoSoRequest;
import com.megacart.dto.request.XacNhanDoiEmailRequest;
import com.megacart.dto.response.AuthResponse;
import com.megacart.dto.response.CapNhatHoSoResponse;
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
     * Cập nhật thông tin hồ sơ một cách thông minh.
     * Tự động xử lý cập nhật thông tin cơ bản và/hoặc khởi tạo luồng thay đổi email.
     * @param taiKhoan Đối tượng TaiKhoan của người dùng đang đăng nhập.
     * @param request Dữ liệu cập nhật.
     * @return Một response cho biết kết quả và hành động tiếp theo (nếu có).
     */
    @PatchMapping("/ho-so")
    public ResponseEntity<CapNhatHoSoResponse> capNhatHoSo(@AuthenticationPrincipal TaiKhoan taiKhoan, @Valid @RequestBody CapNhatHoSoRequest request) {
        CapNhatHoSoResponse response = khachHangService.capNhatHoSo(taiKhoan.getMaTaiKhoan(), request);
        return ResponseEntity.ok(response);
    }

    /*
     * Endpoint này vẫn cần thiết để hoàn tất việc thay đổi email.
     * Nó được gọi sau khi người dùng nhận được OTP từ việc cập nhật hồ sơ.
     * Bước 2: Gửi OTP để xác nhận và hoàn tất việc thay đổi email.
     */
    @PostMapping("/email/xac-nhan")
    public ResponseEntity<AuthResponse> xacNhanDoiEmail(@AuthenticationPrincipal TaiKhoan taiKhoan, @Valid @RequestBody XacNhanDoiEmailRequest request) {
        AuthResponse response = khachHangService.xacNhanDoiEmail(taiKhoan.getMaTaiKhoan(), request.getOtp());
        return ResponseEntity.ok(response);
    }
}