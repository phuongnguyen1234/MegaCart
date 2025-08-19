package com.megacart.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class KhoResponse {
    private Integer maSanPham;
    private String tenSanPham;
    private String danhMucCha;
    private String danhMucCon;
    private Integer soLuong;
    private String noiDungCapNhat;
    private String thongBao;
    private String anhMinhHoaChinh;
}