package com.megacart.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean("tokenBlacklistCache")
    public Cache<String, Boolean> tokenBlacklistCache() {
        // Cấu hình cache:
        // - Tự động xóa entry sau 24 giờ (phải lớn hơn hoặc bằng thời gian sống của JWT)
        // - Giới hạn tối đa 10,000 token để tránh tràn bộ nhớ
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();
    }
}