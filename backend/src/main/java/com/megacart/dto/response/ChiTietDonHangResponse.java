package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietDonHangResponse {
    private Integer maDonHang;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiDatHang;
    private LocalDateTime thoiGianDatHang;
    private String trangThai;
    private String hinhThucGiaoHang;
    private String hinhThucThanhToan;
    private String trangThaiThanhToan;
    private long tongTien;

    // Fields for specific statuses
    private LocalDateTime duKienGiaoHang;
    private String ghiChu;
    private LocalDateTime thoiGianThanhToan;

    private List<ItemResponse> items;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemResponse {
        private Integer maSanPham;
        private String tenSanPham;
        private String anhMinhHoa;
        private long donGia;
        private int soLuong;
        private String trangThaiItem; // e.g., "Hết hàng"
    }
}