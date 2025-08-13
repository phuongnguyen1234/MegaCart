package com.megacart.dto.request;

import com.megacart.enumeration.ViTri;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CapNhatNhanVienRequest {
    // Tất cả các trường đều là optional cho một yêu cầu PATCH
    @Size(min = 2, message = "Tên phải có ít nhất 2 ký tự")
    private String hoTen;

    @Email(message = "Email không hợp lệ.")
    private String email;

    @Size(min = 10, max = 11, message = "Số điện thoại không hợp lệ.")
    private String soDienThoai;

    private ViTri viTri;
}