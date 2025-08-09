package com.megacart.service;

import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.ThemVaoGioHangResponse;
import com.megacart.dto.response.ThongTinThanhToanResponse;
import com.megacart.model.TaiKhoan;

public interface GioHangService {
    ThemVaoGioHangResponse themVaoGioHang(ThemVaoGioHangRequest request, TaiKhoan taiKhoan);

    ThongTinThanhToanResponse getThongTinThanhToan(TaiKhoan taiKhoan);
}