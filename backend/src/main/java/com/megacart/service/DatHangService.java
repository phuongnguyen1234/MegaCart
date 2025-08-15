package com.megacart.service;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.model.TaiKhoan;

public interface DatHangService {
    /**
     * Tạo một đơn hàng mới từ thông tin yêu cầu.
     * @param request DTO chứa thông tin đơn hàng.
     * @param taiKhoan Tài khoản của khách hàng đang đặt hàng.
     * @return Một response chứa thông tin về đơn hàng vừa tạo.
     */
    DatHangResponse taoDonHang(DatHangRequest request, TaiKhoan taiKhoan);
}