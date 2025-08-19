package com.megacart.dto.response;

import com.megacart.enumeration.TrangThaiThanhToan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DonHangDangGiaoQuanLyResponse {
    private Integer maDonHang;
    private String tenNhanVienGiaoHang;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiNhanHang;
    private int tongTien;
    private TrangThaiThanhToan trangThaiThanhToan;
}
