package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiDonHang;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DonHangQuanLyResponse {
    private Integer maDonHang;
    private String tenKhachHang;
    private Instant thoiGianDatHang;
    private TrangThaiDonHang trangThai;
    private int tongTien;
}