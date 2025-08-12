package com.megacart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CapNhatSoLuongRequest {
    @NotNull(message = "Số lượng không được để trống.")
    @Min(value = 0, message = "Số lượng không được là số âm.")
    private Integer soLuong;
}