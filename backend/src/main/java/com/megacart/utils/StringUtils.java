package com.megacart.utils;

import java.util.Objects;

public final class StringUtils {

    private StringUtils() {
        // Lớp tiện ích không nên được khởi tạo
    }

    /**
     * Kiểm tra xem một chuỗi mới có khác với chuỗi cũ và không rỗng/trắng hay không.
     * So sánh không phân biệt hoa thường.
     * @param newValue Giá trị mới.
     * @param oldValue Giá trị cũ.
     * @return true nếu giá trị mới hợp lệ và đã thay đổi, ngược lại là false.
     */
    public static boolean isNotBlankAndChanged(String newValue, String oldValue) {
        return newValue != null && !newValue.isBlank() && !newValue.equalsIgnoreCase(oldValue);
    }
}
