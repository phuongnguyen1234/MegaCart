package com.megacart.dto.request;

import com.megacart.enumeration.TrangThaiDanhMuc;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ThemDanhMucRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String tenDanhMuc;

    // ID của danh mục cha, có thể null nếu là danh mục gốc
    private Integer maDanhMucCha;

    // Trạng thái, có thể null, mặc định sẽ là HOAT_DONG
    private TrangThaiDanhMuc trangThai;
}