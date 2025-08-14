package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HinhThucThanhToan {
    THANH_TOAN_KHI_NHAN_HANG("Thanh toán khi nhận hàng");

    private final String tenHienThi;

    @JsonCreator
    public static HinhThucThanhToan fromString(String text) {
        if (text == null) {
            return null;
        }
        for (HinhThucThanhToan b : HinhThucThanhToan.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy hình thức thanh toán hợp lệ: '" + text + "'");
    }
}
