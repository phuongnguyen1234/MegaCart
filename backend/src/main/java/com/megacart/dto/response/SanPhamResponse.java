package com.megacart.dto.response;

import com.megacart.enumeration.DonVi;
import com.megacart.enumeration.NhanSanPham;
import com.megacart.enumeration.TrangThaiTonKho;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SanPhamResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private Integer donGia;
    private DonVi donVi;
    private String nhaSanXuat;
    private NhanSanPham nhan;
    private TrangThaiTonKho trangThaiTonKho; // Trạng thái còn hàng/hết hàng
    private String anhMinhHoaChinh; // URL đến ảnh đại diện của sản phẩm
}