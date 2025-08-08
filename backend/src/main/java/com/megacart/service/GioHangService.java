package com.megacart.service;

import com.megacart.dto.request.ThemVaoGioHangRequest;
import com.megacart.dto.response.GioHangResponse;
import com.megacart.dto.response.ThongTinThanhToanResponse;
import com.megacart.model.TaiKhoan;

public interface GioHangService {
    GioHangResponse themVaoGioHang(ThemVaoGioHangRequest request, TaiKhoan taiKhoan);

    ThongTinThanhToanResponse getThongTinThanhToan(TaiKhoan taiKhoan);
}