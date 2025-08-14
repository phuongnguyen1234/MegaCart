package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDanhMuc {
    HOAT_DONG("Hoạt động"),
    KHONG_HOAT_DONG("Không hoạt động");

    private final String tenHienThi;
}