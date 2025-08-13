package com.megacart.dto.request;

import com.megacart.enumeration.ViTri;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ThemNhanVienRequest {
    @NotBlank(message = "Tên nhân viên không được để trống.")
    private String hoTen;

    @Email(message = "Email không hợp lệ.")
    @NotBlank(message = "Email không được để trống.")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Size(min = 10, max = 11, message = "Số điện thoại không hợp lệ.")
    private String soDienThoai;

    @NotNull(message = "Vị trí không được để trống.")
    private ViTri viTri;
}