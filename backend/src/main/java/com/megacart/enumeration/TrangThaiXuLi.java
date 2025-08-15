package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiXuLi implements DisplayableEnum {
    DANG_XU_LY("Đang xử lý"),
    CHO_XU_LY("Chờ xử lý");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiXuLi fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiXuLi b : TrangThaiXuLi.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái xử lí hợp lệ: '" + text + "'");
    }
}
