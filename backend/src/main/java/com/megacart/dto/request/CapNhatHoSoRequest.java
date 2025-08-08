package com.megacart.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CapNhatHoSoRequest {
    // Các trường này là optional, frontend chỉ gửi lên những gì người dùng thay đổi
    @Size(min = 2, message = "Tên phải có ít nhất 2 ký tự")
    private String tenKhachHang;

    @Size(min = 10, max = 11, message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    private String diaChi;

    @Email(message = "Email mới không hợp lệ")
    private String emailMoi;
}