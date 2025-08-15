package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiDanhMuc;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChiTietDanhMucQuanLyResponse {
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private Integer maDanhMucCha; // Trả về ID để dễ dàng chọn trong combobox
    private TrangThaiDanhMuc trangThai;
}