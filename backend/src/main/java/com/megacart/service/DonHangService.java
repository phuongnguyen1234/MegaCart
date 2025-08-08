package com.megacart.service;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.model.TaiKhoan;

public interface DonHangService {
    DatHangResponse datHang(DatHangRequest request, TaiKhoan taiKhoan);
}