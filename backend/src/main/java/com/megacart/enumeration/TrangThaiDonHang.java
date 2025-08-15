package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDonHang implements DisplayableEnum {
    CHO_XAC_NHAN("Chờ xác nhận"),
    CHO_XU_LY("Chờ xử lý"),
    DANG_GIAO("Đang giao"),
    DA_GIAO("Đã giao"),
    DA_HUY("Đã hủy");

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiDonHang fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiDonHang b : TrangThaiDonHang.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái đơn hàng hợp lệ: '" + text + "'");
    }
}
