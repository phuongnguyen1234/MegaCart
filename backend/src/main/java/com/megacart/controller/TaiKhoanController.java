package com.megacart.controller;

import com.megacart.dto.request.DangKyRequest;
import com.megacart.dto.request.QuenMatKhauRequest;
import com.megacart.dto.request.XacThucOtpRequest;
import com.megacart.dto.request.DatLaiMatKhauRequest;
import com.megacart.dto.request.DangNhapRequest;
import com.megacart.dto.response.AuthResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.service.TaiKhoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;

    @PostMapping("/dang-ky")
    public ResponseEntity<AuthResponse> dangKy(@Valid @RequestBody DangKyRequest request) {
        return ResponseEntity.ok(taiKhoanService.dangKy(request));
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<AuthResponse> dangNhap(@Valid @RequestBody DangNhapRequest request) {
        return ResponseEntity.ok(taiKhoanService.xacThucTaiKhoan(request));
    }

    @PostMapping("/dang-xuat")
    public ResponseEntity<MessageResponse> dangXuat(@RequestHeader("Authorization") String authHeader) {
        taiKhoanService.dangXuat(authHeader);
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công."));
    }

    @PostMapping("/quen-mat-khau")
    public ResponseEntity<MessageResponse> quenMatKhau(@Valid @RequestBody QuenMatKhauRequest request) {
        taiKhoanService.quenMatKhau(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Nếu email của bạn tồn tại trong hệ thống, một mã OTP đã được gửi đến."));
    }

    @PostMapping("/xac-thuc-otp")
    public ResponseEntity<MessageResponse> xacThucOtp(@Valid @RequestBody XacThucOtpRequest request) {
        taiKhoanService.xacThucOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(new MessageResponse("Xác thực OTP thành công."));
    }

    @PostMapping("/dat-lai-mat-khau")
    public ResponseEntity<MessageResponse> datLaiMatKhau(@Valid @RequestBody DatLaiMatKhauRequest request) {
        taiKhoanService.datLaiMatKhau(request.getEmail(), request.getOtp(), request.getMatKhauMoi());
        return ResponseEntity.ok(new MessageResponse("Đặt lại mật khẩu thành công."));
    }
}