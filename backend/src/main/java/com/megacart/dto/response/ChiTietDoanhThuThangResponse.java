package com.megacart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChiTietDoanhThuThangResponse {
    private String thang; // Format "MM/YYYY"
    private Integer mucTieu;
    private Integer doanhThu;
    private Double tiLeDatMucTieu; // Dạng phần trăm
    private Double tangTruong; // Dạng phần trăm
    private BigDecimal trungBinhMoiDon;
}
