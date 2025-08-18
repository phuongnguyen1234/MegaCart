package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietDonHangQuanLyResponse {
    private Integer maDonHang;
    private long tongTien;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiNhanHang;
    private Instant thoiGianDatHang;
    private HinhThucNhanHang hinhThucGiaoHang;
    private HinhThucThanhToan hinhThucThanhToan;
    private TrangThaiDonHang trangThai;
    private TrangThaiThanhToan trangThaiThanhToan;
    private LocalDateTime duKienGiaoHang;
    private String ghiChu;
    private List<Item> items;
    private String thongBao;

    @Data
    @Builder
    public static class Item {
        private Integer maSanPham;
        private String tenSanPham;
        private String anhMinhHoaChinh;
        private int donGia;
        private String donVi;
        private int soLuong;
        private long tongTien;
    }
}
