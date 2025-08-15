package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiXacThuc implements DisplayableEnum {
    DAT_LAI_MAT_KHAU("Đặt lại mật khẩu"),
    CAP_NHAT_EMAIL("Cập nhật email");

    private final String tenHienThi;

    @JsonCreator
    public static LoaiXacThuc fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (LoaiXacThuc b : LoaiXacThuc.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại xác thực hợp lệ: '" + text + "'");
    }
}