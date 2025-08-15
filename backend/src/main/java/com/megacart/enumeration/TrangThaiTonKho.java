package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiTonKho implements DisplayableEnum {
    CON_HANG("Còn hàng"),
    HET_HANG("Hết hàng");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiTonKho fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiTonKho b : TrangThaiTonKho.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái tồn kho hợp lệ: '" + text + "'");
    }
}