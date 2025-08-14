package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiTonKho {
    CON_HANG("Còn hàng"),
    HET_HANG("Hết hàng");

    private final String tenHienThi;
}