package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiTonKho;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietSanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private int donGia;
    private String donVi;
    private String nhaSanXuat;
    private String moTa;
    private String ghiChu;
    private TrangThaiTonKho trangThaiTonKho;
    private List<AnhMinhHoaResponse> anhMinhHoas;
    private boolean banChay;
    private List<BreadcrumbItem> breadcrumbs;
    private NhanSanPham nhan;
}