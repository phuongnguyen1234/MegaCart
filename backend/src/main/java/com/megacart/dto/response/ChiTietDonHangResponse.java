package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ hiển thị các trường có giá trị, không hiển thị trường null
public class ChiTietDonHangResponse {

    // Thông tin chung của đơn hàng
    private Integer maDonHang;
    private String tenNguoiNhan;
    private String sdtNhanHang;
    private String diaChiDatHang;
    private LocalDateTime thoiGianDatHang;
    private TrangThaiDonHang trangThai;
    private HinhThucNhanHang hinhThucGiaoHang;
    private HinhThucThanhToan hinhThucThanhToan;
    private TrangThaiThanhToan trangThaiThanhToan;

    // Các trường thông tin thay đổi theo trạng thái đơn hàng
    private LocalDateTime duKienGiaoHang;
    private LocalDateTime thoiGianThanhToan;
    private String ghiChu; // Dùng cho lý do hủy đơn hoặc ghi chú của đơn hàng


    // Thông tin thanh toán
    private long tongTien;

    // Danh sách các sản phẩm trong đơn hàng
    private List<ItemResponse> items;

    /**
     * DTO con đại diện cho một sản phẩm trong trang chi tiết đơn hàng.
     */
    @Data
    @Builder
    public static class ItemResponse {
        private Integer maSanPham;
        private String tenSanPham;
        private String anhMinhHoa;
        private int donGia;
        private int soLuong;
        private String trangThaiItem; // Ví dụ: "Hết hàng"
        // Frontend sẽ tự tính thành tiền = donGia * soLuong
    }
}