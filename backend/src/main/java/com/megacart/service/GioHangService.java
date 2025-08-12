package com.megacart.service;

import com.megacart.dto.request.CapNhatSoLuongRequest;
import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.GioHangResponse;
import com.megacart.dto.response.ThemVaoGioHangResponse;
import com.megacart.dto.response.ThongTinThanhToanResponse;
import com.megacart.dto.response.XoaKhoiGioHangResponse;
import com.megacart.model.TaiKhoan;

public interface GioHangService {
    ThemVaoGioHangResponse themVaoGioHang(ThemVaoGioHangRequest request, TaiKhoan taiKhoan);

    ThongTinThanhToanResponse getThongTinThanhToan(TaiKhoan taiKhoan);

    XoaKhoiGioHangResponse xoaKhoiGioHang(Integer maSanPham, TaiKhoan taiKhoan);

    GioHangResponse capNhatSoLuong(Integer maSanPham, CapNhatSoLuongRequest request, TaiKhoan taiKhoan);

    void xoaToanBoGioHang(TaiKhoan taiKhoan);
}