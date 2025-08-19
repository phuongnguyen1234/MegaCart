package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietDonHangGiaoHangResponse {
    private Integer maDonHang;
    private long tongTien;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiNhanHang;
    private LocalDateTime thoiGianDatHang;
    private HinhThucNhanHang hinhThucGiaoHang;
    private HinhThucThanhToan hinhThucThanhToan;
    private String ghiChu;
    private List<Item> items;

    @Data
    @Builder
    public static class Item {
        private String tenSanPham;
        private String anhMinhHoaChinh;
        private int donGia;
        private int soLuong;
        private long tongTienSanPham;
    }
}