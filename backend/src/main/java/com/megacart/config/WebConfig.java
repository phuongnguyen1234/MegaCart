package com.megacart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Áp dụng cho tất cả các endpoint bắt đầu bằng /api/
                        .allowedOrigins("http://localhost:3000", "http://localhost:5173") // **QUAN TRỌNG**: URL của frontend
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Các phương thức cho phép
                        .allowedHeaders("*") // Cho phép tất cả các header
                        .allowCredentials(true); // Cho phép gửi cookie và header Authorization
            }
        };
    }
}