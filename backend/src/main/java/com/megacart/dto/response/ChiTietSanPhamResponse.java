package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiTonKho;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ChiTietSanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private long donGia;
    private String donVi;
    private String nhaSanXuat;
    private String moTa;
    private String ghiChu;
    private TrangThaiTonKho trangThaiTonKho;
    private boolean banChay;
    private List<AnhMinhHoaResponse> anhMinhHoas;
    private List<BreadcrumbItem> breadcrumbs;
}