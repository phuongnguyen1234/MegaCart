package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HinhThucNhanHang {
    GIAO_HANG_TAN_NHA("Giao hàng tận nhà");

    private final String tenHienThi;

    @JsonCreator
    public static HinhThucNhanHang fromString(String text) {
        if (text == null) {
            return null;
        }
        for (HinhThucNhanHang b : HinhThucNhanHang.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy hình thức nhận hàng hợp lệ: '" + text + "'");
    }
}
