package com.megacart.service;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.model.TaiKhoan;

public interface DatHangService {

    /**
     * Xử lý quy trình nghiệp vụ phức tạp của việc đặt hàng.
     *
     * @param taiKhoan Người dùng đang thực hiện đặt hàng.
     * @param request  Thông tin địa chỉ giao hàng và ghi chú.
     * @return Phản hồi chứa thông tin về đơn hàng vừa tạo.
     */
    DatHangResponse datHang(DatHangRequest request, TaiKhoan taiKhoan);
}