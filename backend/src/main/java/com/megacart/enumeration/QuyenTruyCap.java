package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuyenTruyCap {
    ADMIN("Admin"),
    KHACH_HANG("Khách hàng"),
    NHAN_VIEN("Nhân viên");

    private final String tenHienThi;
}
