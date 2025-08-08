package com.megacart.enumeration;

/**
 * Enum này thể hiện trạng thái kinh doanh của một sản phẩm.
 * Trạng thái tồn kho (còn hàng/hết hàng) sẽ được suy ra từ số lượng trong kho.
 */
public enum TrangThaiSanPham {
    DANG_BAN, // Sản phẩm đang được hiển thị và bán trên trang web
    AN, // Sản phẩm bị ẩn khỏi trang web, không bán nhưng vẫn còn trong hệ thống
    NGUNG_KINH_DOANH // Sản phẩm đã ngừng kinh doanh vĩnh viễn
}