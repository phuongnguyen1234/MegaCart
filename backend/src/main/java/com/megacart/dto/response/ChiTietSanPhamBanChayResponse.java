package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChiTietSanPhamBanChayResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private Long soLuongBanRa;
    private Double soLuongTrungBinhMoiDon;
    private Long soDonDat;
}