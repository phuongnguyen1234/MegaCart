package com.megacart.dto.response;

import com.megacart.enumeration.DonVi;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GioHangResponse {
    private List<ChiTietGioHangItem> items;
    private int tongSoLuongSanPham; // Tổng số lượng các sản phẩm khác nhau trong giỏ
    private long tongTien;

    @Data
    @Builder
    public static class ChiTietGioHangItem {
        private Integer maSanPham;
        private String tenSanPham;
        private String anhMinhHoa;
        private Integer donGia;
        private DonVi donVi;
        private Integer soLuong;
        private long thanhTien;
    }
}