package com.megacart.dto.request;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CapNhatSanPhamRequest {
    // Các trường thông tin cơ bản (tùy chọn)
    // Nếu giá trị được cung cấp, nó không được rỗng
    @Size(min = 1, message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    private Integer maDanhMuc;

    private String moTa;

    @Size(min = 1, message = "Nhà sản xuất không được để trống")
    private String nhaSanXuat;

    @Min(value = 0, message = "Đơn giá không được âm")
    private Integer donGia;

    @Size(min = 1, message = "Đơn vị không được để trống")
    private String donVi;

    private NhanSanPham nhan;
    private String ghiChu;

    private TrangThaiSanPham trangThai;

    // Quản lý ảnh
    private List<String> urlsAnhXoa;
    private String anhChinhIdentifier; // URL của ảnh cũ hoặc tên file của ảnh mới sẽ làm ảnh chính
}