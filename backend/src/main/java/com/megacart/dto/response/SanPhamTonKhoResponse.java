package com.megacart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamTonKhoResponse {
    private String tenSanPham;
    private Integer soLuongTon;
    private Long soLuongDaBan; // Số lượng đã bán có thể lớn, dùng Long
}
