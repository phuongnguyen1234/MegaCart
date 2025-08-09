package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.TrangThaiDonHang;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các trường null khi serialize, ví dụ: thongTinThem
public class LichSuDonHangResponse {
    private Integer maDonHang;
    private TrangThaiDonHang trangThai;
    private LocalDateTime thoiGianDatHang;
    private long tongTien;

    /**
     * Thông tin tóm tắt của sản phẩm đầu tiên trong đơn hàng để hiển thị trên danh sách.
     */
    private String tenSanPhamDauTien;
    private String anhMinhHoaDauTien;
    private int soLuongDauTien;

    // Số lượng các loại sản phẩm khác trong đơn hàng (không bao gồm sản phẩm đầu tiên)
    private Integer soLuongLoaiSanPhamKhac; // Ví dụ: 2
}
