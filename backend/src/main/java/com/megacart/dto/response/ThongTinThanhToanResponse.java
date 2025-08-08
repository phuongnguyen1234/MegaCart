package com.megacart.dto.response;

import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ThongTinThanhToanResponse {
    // Thông tin giỏ hàng
    private List<GioHangResponse.ChiTietGioHangItem> items;
    private int tongSoLuongSanPham;
    private long tongTien;

    // Thông tin mặc định của khách hàng để điền vào form
    private ThongTinKhachHangResponse thongTinGiaoHangMacDinh;

    // Các tùy chọn cho form để frontend có thể render các dropdown/radio button
    private List<HinhThucNhanHang> hinhThucNhanHangOptions;
    private List<HinhThucThanhToan> hinhThucThanhToanOptions;
}