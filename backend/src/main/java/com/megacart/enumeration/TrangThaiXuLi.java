package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiXuLi {
    DANG_XU_LY("Đang xử lý"),
    CHO_XU_LY("Chờ xử lý");

    private final String tenHienThi;
}
