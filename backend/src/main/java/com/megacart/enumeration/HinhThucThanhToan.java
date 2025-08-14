package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HinhThucThanhToan {
    THANH_TOAN_KHI_NHAN_HANG("Thanh toán khi nhận hàng");

    private final String tenHienThi;
}
