package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HienThiDanhSachKhachHangResponse {
    private Integer maKhachHang;
    private String tenKhachHang;
    private String email;
    private String diaChi;
    private String soDienThoai;
    private String trangThaiTaiKhoan;
}