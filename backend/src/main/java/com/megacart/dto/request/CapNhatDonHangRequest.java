package com.megacart.dto.request;

import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CapNhatDonHangRequest {
    private TrangThaiDonHang trangThai;
    private TrangThaiThanhToan trangThaiThanhToan;

    @FutureOrPresent(message = "Ngày giao hàng dự kiến không thể là một ngày trong quá khứ.")
    private LocalDateTime duKienGiaoHang;

    private String ghiChu;
}