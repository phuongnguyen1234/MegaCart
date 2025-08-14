package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiXacThuc {
    DAT_LAI_MAT_KHAU("Đặt lại mật khẩu"),
    CAP_NHAT_EMAIL("Cập nhật email");

    private final String tenHienThi;
}