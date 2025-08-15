package com.megacart.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.megacart.config.jackson.DisplayableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiSanPham implements DisplayableEnum {
    BAN("Bán"), // Sản phẩm đang được hiển thị và bán trên trang web
    KHONG_BAN("Không bán"); // Sản phẩm không được hiển thị và bán trên trang web (bao gồm cả ẩn và ngừng kinh doanh)

    private final String tenHienThi;

    @JsonCreator
    public static TrangThaiSanPham fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (TrangThaiSanPham b : TrangThaiSanPham.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái sản phẩm hợp lệ: '" + text + "'");
    }
}