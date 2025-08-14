package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HienThiDanhSachNhanVienResponse {
    private Integer maNhanVien;
    private String tenNhanVien;
    private String email;
    private String soDienThoai;
    private String trangThaiTaiKhoan;
    private String viTri;
}