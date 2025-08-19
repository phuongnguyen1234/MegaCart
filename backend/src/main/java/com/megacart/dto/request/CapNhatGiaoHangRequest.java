package com.megacart.dto.request;

import com.megacart.enumeration.KetQuaGiaoHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CapNhatGiaoHangRequest {
    @NotNull(message = "Kết quả giao hàng không được để trống.")
    private KetQuaGiaoHang ketQua;

    // Chỉ bắt buộc khi kết quả là THẤT BẠI
    private String lyDoThatBai;

    // Cho phép shipper cập nhật trạng thái thanh toán, đặc biệt cho đơn COD
    private TrangThaiThanhToan trangThaiThanhToan;
}