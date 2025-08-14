package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ViTri {
    NHAN_VIEN_GIAO_HANG("Nhân viên giao hàng"),
    NHAN_VIEN_QUAN_LI_DON("Nhân viên quản lý đơn"),
    NHAN_VIEN_QUAN_LI_KHO("Nhân viên quản lý kho");

    private final String tenHienThi;
}
