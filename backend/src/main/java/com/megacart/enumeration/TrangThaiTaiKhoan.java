package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiTaiKhoan {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiTaiKhoan fromString(String text) {
        if (text == null) {
            return null;
        }
        for (TrangThaiTaiKhoan b : TrangThaiTaiKhoan.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái tài khoản hợp lệ: '" + text + "'");
    }
}