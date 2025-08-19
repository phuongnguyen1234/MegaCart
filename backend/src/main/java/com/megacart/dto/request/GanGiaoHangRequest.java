package com.megacart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GanGiaoHangRequest {
    @NotNull(message = "Mã nhân viên giao hàng không được để trống.")
    private Integer maNhanVienGiaoHang;
}