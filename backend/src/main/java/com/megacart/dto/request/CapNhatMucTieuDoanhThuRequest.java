package com.megacart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CapNhatMucTieuDoanhThuRequest {
    @NotNull(message = "Mục tiêu mới không được để trống.")
    @Min(value = 0, message = "Mục tiêu không được là số âm.")
    private Long mucTieuMoi;
}
