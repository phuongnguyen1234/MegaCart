package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiDonHang;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatHangResponse {
    private Integer maDonHang;
    private TrangThaiDonHang trangThai;
    private String thongBao; // Ví dụ: "Đặt hàng thành công!" hoặc "Một số sản phẩm đã hết hàng, đơn hàng của bạn đang chờ xác nhận."
}