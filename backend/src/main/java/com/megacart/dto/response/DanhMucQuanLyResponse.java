package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiDanhMuc;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DanhMucQuanLyResponse {
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private String tenDanhMucCha; // Có thể null nếu là danh mục gốc
    private TrangThaiDanhMuc trangThai;
}