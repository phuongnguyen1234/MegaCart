package com.megacart.dto.request;

import com.megacart.enumeration.TrangThaiTaiKhoan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CapNhatTrangThaiTaiKhoanRequest {
    @NotNull(message = "Trạng thái tài khoản không được để trống.")
    private TrangThaiTaiKhoan trangThai;
}