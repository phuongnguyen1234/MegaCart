package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietGiaoHangQuanLyResponse {
    private Integer maDonHang;
    private String tenNguoiNhan;
    private String diaChiNhanHang;
    private Integer maNhanVienGiaoHang; // ID của nhân viên hiện tại, có thể null
    private String tenNhanVienGiaoHang; // Tên của nhân viên hiện tại, có thể null
}