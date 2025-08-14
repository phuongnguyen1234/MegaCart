package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ViTri {
    NHAN_VIEN_GIAO_HANG("Nhân viên giao hàng"),
    NHAN_VIEN_QUAN_LI_DON("Nhân viên quản lý đơn"),
    NHAN_VIEN_QUAN_LI_KHO("Nhân viên quản lý kho");

    private final String tenHienThi;

    @JsonCreator
    public static ViTri fromString(String text) {
        if (text == null) {
            return null;
        }
        for (ViTri b : ViTri.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy vị trí hợp lệ: '" + text + "'");
    }
}
