package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NhanSanPham implements DisplayableEnum {
    MOI("Mới");

    private final String tenHienThi;

    @JsonCreator
    public static NhanSanPham fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (NhanSanPham b : NhanSanPham.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy nhãn sản phẩm hợp lệ: '" + text + "'");
    }
}