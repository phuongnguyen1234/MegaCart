package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiThanhToan implements DisplayableEnum {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    DA_THANH_TOAN("Đã thanh toán");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiThanhToan fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiThanhToan b : TrangThaiThanhToan.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái thanh toán hợp lệ: '" + text + "'");
    }
}
