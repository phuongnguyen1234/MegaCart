package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiThanhToan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DonHangGiaoHangResponse {
    private Integer maDonHang;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiNhanHang;
    private int tongTien;
    private TrangThaiThanhToan trangThaiThanhToan;
}