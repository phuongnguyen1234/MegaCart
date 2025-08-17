package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.enumeration.ViTri;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HienThiDanhSachNhanVienResponse {
    private Integer maNhanVien;
    private String tenNhanVien;
    private String email;
    private String soDienThoai;
    private TrangThaiTaiKhoan trangThaiTaiKhoan;
    private ViTri viTri;
    private String thongBao;
}