package com.megacart.service;

import com.megacart.dto.request.CapNhatHoSoRequest;
import com.megacart.dto.response.CapNhatHoSoResponse;
import com.megacart.dto.response.AuthResponse;
import com.megacart.dto.response.ThongTinKhachHangResponse;

public interface KhachHangService {
    ThongTinKhachHangResponse getThongTinKhachHang(Integer maKhachHang);

    CapNhatHoSoResponse capNhatHoSo(Integer maKhachHang, CapNhatHoSoRequest request);

    AuthResponse xacNhanDoiEmail(Integer maKhachHang, String otp);

    void guiOtpThayDoiEmail(String toEmail, String otp);

}