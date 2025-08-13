package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiTonKho;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChiTietSanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private Integer donGia;
    private String donVi;
    private String nhaSanXuat;
    private String moTa;
    private String ghiChu;
    private TrangThaiTonKho trangThaiTonKho;
    private List<AnhMinhHoaResponse> anhMinhHoas;
    private List<BreadcrumbItem> breadcrumbs;
}