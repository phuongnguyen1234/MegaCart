package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiTaiKhoan implements DisplayableEnum {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiTaiKhoan fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiTaiKhoan b : TrangThaiTaiKhoan.values()) {
            // Cho phép nhận cả tên hiển thị (e.g., "Hoạt động") và tên hằng số (e.g., "HOAT_DONG")
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái tài khoản hợp lệ: '" + text + "'");
    }
}