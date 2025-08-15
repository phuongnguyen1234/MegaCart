package com.megacart.config.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Một Jackson Module để đăng ký custom serializer cho DisplayableEnum.
 * Spring Boot sẽ tự động phát hiện và sử dụng Module này.
 */
public class CustomEnumModule extends SimpleModule {
    public CustomEnumModule() {
        super("CustomEnumModule");
        // Đăng ký serializer cho tất cả các lớp implement DisplayableEnum
        addSerializer(DisplayableEnum.class, new DisplayableEnumSerializer());
    }
}