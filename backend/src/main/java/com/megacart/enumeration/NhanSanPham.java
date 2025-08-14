package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NhanSanPham {
    MOI("Mới"),
    BAN_CHAY("Bán chạy");

    private final String tenHienThi;
}