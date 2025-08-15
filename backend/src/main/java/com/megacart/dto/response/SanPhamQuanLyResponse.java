package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiSanPham;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SanPhamQuanLyResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private String danhMucCha;
    private String danhMucCon;
    private int donGia;
    private TrangThaiSanPham trangThai;
}