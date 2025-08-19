package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThemSanPhamAsyncResponse {
    private Integer maSanPham;
    private String thongBao;
}
