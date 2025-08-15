package com.megacart.dto.response;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiTonKho;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private int donGia;
    private String donVi;
    private String nhaSanXuat;
    private NhanSanPham nhan;
    private TrangThaiTonKho trangThaiTonKho;
    private String anhMinhHoaChinh;
    private boolean banChay;
}