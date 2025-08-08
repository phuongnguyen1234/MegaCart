package com.megacart.service;

import com.megacart.dto.request.DangKyRequest;
import com.megacart.dto.request.DangNhapRequest;
import com.megacart.dto.response.AuthResponse;

public interface TaiKhoanService {
    AuthResponse dangKy(DangKyRequest request);
    AuthResponse dangNhap(DangNhapRequest request);
    void dangXuat(String authHeader);
    void quenMatKhau(String email);
    void xacThucOtp(String email, String otp);
    void datLaiMatKhau(String email, String otp, String newPassword);
}
