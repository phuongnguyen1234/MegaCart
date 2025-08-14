package com.megacart.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum này thể hiện trạng thái kinh doanh của một sản phẩm.
 * Trạng thái tồn kho (còn hàng/hết hàng) sẽ được suy ra từ số lượng trong kho.
 */
@Getter
@RequiredArgsConstructor
public enum TrangThaiSanPham {
    BAN("Bán"), // Sản phẩm đang được hiển thị và bán trên trang web
    KHONG_BAN("Không bán"); // Sản phẩm không được hiển thị và bán trên trang web (bao gồm cả ẩn và ngừng kinh doanh)

    private final String tenHienThi;
}