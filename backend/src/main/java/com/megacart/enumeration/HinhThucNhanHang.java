package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HinhThucNhanHang {
    GIAO_HANG_TAN_NHA("Giao hàng tận nhà");

    private final String tenHienThi;
}
