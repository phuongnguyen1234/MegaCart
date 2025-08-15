package com.megacart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DanhMucOptionResponse {
    private Integer maDanhMuc;
    private String tenDanhMucHienThi; // Ví dụ: "  - Áo Sơ Mi"
}