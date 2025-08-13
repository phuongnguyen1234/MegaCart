package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

import com.megacart.enumeration.TrangThaiTaiKhoan;

@Data
@Builder
public class HienThiDanhSachKhachHangResponse {
    private Integer maKhachHang;
    private String tenKhachHang;
    private String email;
    private String diaChi;
    private String soDienThoai;
    private TrangThaiTaiKhoan trangThaiTaiKhoan;
}