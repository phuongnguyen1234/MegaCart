package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThemVaoGioHangResponse {
    private String message;
    private int tongSoLuongSanPham; // Tổng số lượng các sản phẩm khác nhau
    private int tongSoLuongDonVi; // Tổng số lượng các đơn vị sản phẩm (e.g., 2 cái A + 3 cái B = 5)
}