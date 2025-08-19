package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiSanPham;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SanPhamQuanLyResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private String anhMinhHoaChinh; // Ảnh để hiển thị trên danh sách quản lý
    private String danhMucCha;
    private String danhMucCon;
    private int donGia;
    private TrangThaiSanPham trangThai;
    private NhanSanPham nhan;
    private String thongBao;
}