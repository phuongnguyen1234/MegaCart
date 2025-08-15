package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiSanPham {
    BAN("Bán"), // Sản phẩm đang được hiển thị và bán trên trang web
    KHONG_BAN("Không bán"); // Sản phẩm không được hiển thị và bán trên trang web (bao gồm cả ẩn và ngừng kinh doanh)

    private final String tenHienThi;
}