package com.megacart.config;

import com.fasterxml.jackson.databind.Module;
import com.megacart.config.jackson.CustomEnumModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình cho thư viện Jackson.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Module customEnumModule() {
        return new CustomEnumModule();
    }
}