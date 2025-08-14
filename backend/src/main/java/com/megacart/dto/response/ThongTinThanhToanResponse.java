package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ThongTinThanhToanResponse {
    private List<GioHangResponse.ChiTietGioHangItem> items;
    private int tongSoLuongSanPham;
    private long tongTien;
    private ThongTinKhachHangResponse thongTinGiaoHangMacDinh;
    private List<EnumOptionResponse> hinhThucNhanHangOptions;
    private List<EnumOptionResponse> hinhThucThanhToanOptions;
}