package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiTaiKhoan {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa");

    private final String tenHienThi;
}