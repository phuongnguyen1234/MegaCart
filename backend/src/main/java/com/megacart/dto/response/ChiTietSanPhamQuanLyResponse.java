package com.megacart.dto.response;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChiTietSanPhamQuanLyResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private Integer maDanhMuc;
    private String moTa;
    private String nhaSanXuat;
    private int donGia;
    private String donVi;
    private NhanSanPham nhan;
    private String ghiChu;
    private TrangThaiSanPham trangThai;
    private List<AnhMinhHoaResponse> anhMinhHoas;
}