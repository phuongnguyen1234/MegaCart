package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HienThiDanhSachKhachHangResponse {
    private Integer maKhachHang;
    private String tenKhachHang;
    private String email;
    private String diaChi;
    private String soDienThoai;
    private TrangThaiTaiKhoan trangThaiTaiKhoan;
    private String thongBao;
}