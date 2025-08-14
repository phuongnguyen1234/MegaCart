package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private long donGia;
    private String donVi;
    private String nhaSanXuat;
    private String nhan;
    private String trangThaiTonKho;
    private String anhMinhHoaChinh;
    private boolean banChay;
}