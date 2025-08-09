package com.megacart.dto.request;

import com.megacart.enumeration.HinhThucNhanHang;
import com.megacart.enumeration.HinhThucThanhToan;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class DatHangRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String tenNguoiNhan;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    private String diaChiNhanHang;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Size(min = 10, max = 11, message = "Số điện thoại không hợp lệ")
    private String sdtNhanHang;

    @NotNull(message = "Hình thức nhận hàng không được để trống")
    private HinhThucNhanHang hinhThucNhanHang;

    @NotNull(message = "Hình thức thanh toán không được để trống")
    private HinhThucThanhToan hinhThucThanhToan;

    @NotEmpty(message = "Phải chọn ít nhất một sản phẩm để đặt hàng")
    @Valid
    private List<SanPhamDatHang> items;

    @Data
    public static class SanPhamDatHang {
        @NotNull(message = "Mã sản phẩm không được để trống")
        private Integer maSanPham;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer soLuong;
    }
}