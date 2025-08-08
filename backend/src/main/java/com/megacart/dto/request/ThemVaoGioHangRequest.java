package com.megacart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThemVaoGioHangRequest {
    @NotNull(message = "Mã sản phẩm không được để trống")
    private Integer maSanPham;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer soLuong;
}