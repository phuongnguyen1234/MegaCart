package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietSanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private long donGia;
    private String donVi;
    private String nhaSanXuat;
    private String moTa;
    private String ghiChu;
    private String trangThaiTonKho;
    private List<AnhMinhHoaResponse> anhMinhHoas;
    @Setter
    private List<BreadcrumbItem> breadcrumbs;
}