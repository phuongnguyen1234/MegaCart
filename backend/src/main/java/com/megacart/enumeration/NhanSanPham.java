package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NhanSanPham {
    MOI("Sản phẩm mới"),
    BAN_CHAY("Bán chạy nhất");

    private final String tenHienThi;
}