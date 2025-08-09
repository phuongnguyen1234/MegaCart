package com.megacart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HuyDonHangRequest {
    @NotBlank(message = "Lý do hủy đơn không được để trống.")
    private String lyDo;
}