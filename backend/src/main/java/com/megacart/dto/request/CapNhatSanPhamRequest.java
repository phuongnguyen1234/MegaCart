package com.megacart.dto.request;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import lombok.Data;

import java.util.List;

@Data
public class CapNhatSanPhamRequest {
    // Các trường thông tin cơ bản (tùy chọn)
    private String tenSanPham;
    private Integer maDanhMuc;
    private String moTa;
    private String nhaSanXuat;
    private Integer donGia;
    private String donVi;
    private NhanSanPham nhan;
    private String ghiChu;
    private TrangThaiSanPham trangThai;

    // Quản lý ảnh
    private List<String> urlsAnhXoa;
    private String anhChinhIdentifier; // URL của ảnh cũ hoặc tên file của ảnh mới sẽ làm ảnh chính
}