package com.megacart.service;

import com.megacart.dto.request.CapNhatThongTinCoBanRequest;
import com.megacart.dto.request.KhoiTaoDoiEmailRequest;
import com.megacart.dto.response.ThongTinKhachHangResponse;

public interface KhachHangService {
    ThongTinKhachHangResponse getThongTinKhachHang(Integer maKhachHang);

    ThongTinKhachHangResponse capNhatThongTinCoBan(Integer maKhachHang, CapNhatThongTinCoBanRequest request);

    void khoiTaoDoiEmail(Integer maKhachHang, KhoiTaoDoiEmailRequest request);

    ThongTinKhachHangResponse xacNhanDoiEmail(Integer maKhachHang, String otp);
}