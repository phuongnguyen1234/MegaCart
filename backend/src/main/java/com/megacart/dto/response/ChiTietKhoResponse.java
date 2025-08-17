package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChiTietKhoResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private Integer soLuongHienTai;
}
