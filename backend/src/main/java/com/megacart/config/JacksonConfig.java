package com.megacart.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.megacart.config.jackson.CustomEnumModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
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

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                // Vì chúng ta đã cài đặt toàn bộ ứng dụng chạy ở UTC,
                // chúng ta có thể giả định rằng mọi giá trị LocalDateTime đều là UTC.
                // Serializer này sẽ định dạng nó thành chuỗi ISO 8601 chuẩn với hậu tố 'Z'.
                gen.writeString(value.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }
        });
    }

}