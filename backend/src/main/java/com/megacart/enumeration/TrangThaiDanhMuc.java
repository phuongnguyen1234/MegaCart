package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDanhMuc implements DisplayableEnum{
    HOAT_DONG("Hoạt động"),
    KHONG_HOAT_DONG("Không hoạt động");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiDanhMuc fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiDanhMuc b : TrangThaiDanhMuc.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái danh mục hợp lệ: '" + text + "'");
    }
}