package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuyenTruyCap implements DisplayableEnum {
    ADMIN("Admin"),
    KHACH_HANG("Khách hàng"),
    NHAN_VIEN("Nhân viên");

    private final String tenHienThi;

    @JsonCreator
    public static QuyenTruyCap fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (QuyenTruyCap b : QuyenTruyCap.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy quyền truy cập hợp lệ: '" + text + "'");
    }
}
