package com.megacart.config.jackson;

/**
 * Một interface chung cho các Enum có tên hiển thị.
 * Bất kỳ Enum nào implement interface này sẽ được tự động serialize
 * thành một đối tượng JSON có cả 'value' và 'label' bởi DisplayableEnumSerializer.
 */
public interface DisplayableEnum {
    String getTenHienThi();
}