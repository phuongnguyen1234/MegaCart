package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NhanSanPham {
    MOI("Mới");

    private final String tenHienThi;
}