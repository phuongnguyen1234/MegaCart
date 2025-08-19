package com.megacart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NhanVienOptionResponse {
    private Integer maNhanVien;
    private String tenNhanVien;
}