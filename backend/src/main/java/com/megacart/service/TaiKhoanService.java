package com.megacart.service;

import com.megacart.dto.request.DangKyRequest;
import com.megacart.dto.request.DangNhapRequest;
import com.megacart.dto.response.AuthResponse;

public interface TaiKhoanService {

    /**
     * Đăng ký một tài khoản khách hàng mới.
     */
    AuthResponse dangKy(DangKyRequest request);

    /**
     * Xác thực thông tin đăng nhập của người dùng và trả về JWT token nếu thành công.
     */
    AuthResponse xacThucTaiKhoan(DangNhapRequest request);

    /**
     * Vô hiệu hóa token hiện tại của người dùng.
     */
    void dangXuat(String authHeader);

    /**
     * Bắt đầu quy trình quên mật khẩu bằng cách gửi OTP đến email người dùng.
     */
    void quenMatKhau(String email);

    /**
     * Xác thực mã OTP mà người dùng cung cấp.
     */
    void xacThucOtp(String email, String otp);

    /**
     * Đặt lại mật khẩu cho người dùng sau khi đã xác thực OTP thành công.
     */
    void datLaiMatKhau(String email, String otp, String newPassword);
}
