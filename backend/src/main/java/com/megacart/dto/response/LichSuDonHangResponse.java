package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiSanPham;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LichSuDonHangResponse {
    private Integer maDonHang;
    private TrangThaiDonHang trangThai;
    private LocalDateTime thoiGianDatHang;
    private long tongTien;
    private String tenSanPhamDauTien;
    private String anhMinhHoaDauTien;
    private int soLuongDauTien;
    private boolean banChayDauTien;
    private TrangThaiSanPham trangThaiSanPhamDauTien; // Thêm trạng thái của sản phẩm
    private Integer soLuongLoaiSanPhamKhac;
}
