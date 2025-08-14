package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDonHang {
    CHO_XAC_NHAN("Chờ xác nhận"),
    CHO_XU_LY("Chờ xử lý"),
    DANG_GIAO("Đang giao"),
    DA_GIAO("Đã giao"),
    DA_HUY("Đã hủy");

    private final String tenHienThi;
}
