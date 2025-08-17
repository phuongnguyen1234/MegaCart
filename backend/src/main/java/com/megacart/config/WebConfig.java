package com.megacart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối đến thư mục upload
        String absoluteUploadPath = Paths.get(uploadDir).toFile().getAbsolutePath();

        // Cấu hình resource handler để phục vụ file từ đường dẫn tuyệt đối
        // Thêm "file:/" để Spring biết đây là một đường dẫn trên hệ thống file
        // URL path /uploads/** sẽ được ánh xạ tới thư mục vật lý đã cấu hình.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + absoluteUploadPath + "/");
    }
}
