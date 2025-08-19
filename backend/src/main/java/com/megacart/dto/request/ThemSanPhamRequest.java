package com.megacart.dto.request;

import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThemSanPhamRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @NotNull(message = "Danh mục không được để trống")
    private Integer maDanhMuc;

    private String moTa;

    @NotBlank(message = "Nhà sản xuất không được để trống")
    private String nhaSanXuat;

    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 0, message = "Đơn giá không được âm")
    private Integer donGia;

    @NotBlank(message = "Đơn vị không được để trống")
    private String donVi;

    private NhanSanPham nhan;

    private String ghiChu;

    @NotNull(message = "Trạng thái không được để trống")
    private TrangThaiSanPham trangThai;

    @NotNull(message = "Vui lòng chỉ định ảnh chính")
    @Min(value = 0, message = "Chỉ số ảnh chính không hợp lệ")
    private Integer anhChinhIndex;
}