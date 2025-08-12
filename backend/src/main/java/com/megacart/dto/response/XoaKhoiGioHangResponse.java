package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XoaKhoiGioHangResponse {
    private String message;
    // Tổng số loại sản phẩm khác nhau trong giỏ hàng
    private int tongSoLuongSanPham;
    // Tổng số lượng của tất cả các sản phẩm (ví dụ: 2 cái bút, 3 quyển vở -> 5)
    private int tongSoLuongDonVi;
}