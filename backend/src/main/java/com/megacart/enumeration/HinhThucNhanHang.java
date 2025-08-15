package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HinhThucNhanHang implements DisplayableEnum {
    GIAO_HANG_TAN_NHA("Giao hàng tận nhà");

    private final String tenHienThi;

    @JsonCreator
    public static HinhThucNhanHang fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (HinhThucNhanHang b : HinhThucNhanHang.values()) {
            // Cho phép nhận cả tên hiển thị và tên hằng số
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy hình thức nhận hàng hợp lệ: '" + text + "'");
    }
}
