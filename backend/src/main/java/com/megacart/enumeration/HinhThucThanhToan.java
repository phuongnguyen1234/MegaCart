package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HinhThucThanhToan implements DisplayableEnum {
    THANH_TOAN_KHI_NHAN_HANG("Thanh toán khi nhận hàng");

    private final String tenHienThi;

    @JsonCreator
    public static HinhThucThanhToan fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (HinhThucThanhToan b : HinhThucThanhToan.values()) {
            // Cho phép nhận cả tên hiển thị và tên hằng số
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy hình thức thanh toán hợp lệ: '" + text + "'");
    }
}
