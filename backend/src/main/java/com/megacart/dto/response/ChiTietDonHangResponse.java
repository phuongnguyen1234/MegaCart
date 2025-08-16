package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.enumeration.TrangThaiTonKho;
import com.megacart.enumeration.TrangThaiSanPham;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietDonHangResponse {
    private Integer maDonHang;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiNhanHang;
    private LocalDateTime thoiGianDatHang;
    private TrangThaiDonHang trangThai;
    private HinhThucNhanHang hinhThucGiaoHang;
    private HinhThucThanhToan hinhThucThanhToan;
    private TrangThaiThanhToan trangThaiThanhToan;
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
        private TrangThaiSanPham trangThaiSanPham; // e.g., "BAN", "KHONG_BAN"
        private TrangThaiTonKho trangThaiTonKho; // e.g., "CON_HANG", "HET_HANG"
    }
}