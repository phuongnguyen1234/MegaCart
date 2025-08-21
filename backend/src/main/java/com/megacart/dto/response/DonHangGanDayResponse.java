package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiDonHang;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DonHangGanDayResponse {
    private Integer maDonHang;
    private String tenKhachHang;
    private LocalDateTime thoiGianDatHang;
    private TrangThaiDonHang trangThai;
    private long tongTien;
}