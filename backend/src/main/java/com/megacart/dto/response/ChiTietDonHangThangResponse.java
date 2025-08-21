package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietDonHangThangResponse {
    private String thang; // Format "MM/YYYY"
    private Integer soDonHang;
    private Double tangTruong; // Dạng phần trăm
    private BigDecimal trungBinhMoiDon;
    private ThongKeTiLe donGiaoThanhCong;
    private ThongKeTiLe donBiHuy;

    @Data
    @Builder
    public static class ThongKeTiLe {
        private Integer soLuong;
        private Double phanTram;
    }
}