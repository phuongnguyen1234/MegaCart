package com.megacart.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
@Profile("dev") // Chỉ chạy component này khi profile 'dev' được kích hoạt
@Slf4j
public class DevBrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    private final String frontendUrl = "http://localhost:5173";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Ứng dụng đã sẵn sàng. Đang thử mở trình duyệt...");
        if (isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(frontendUrl));
                log.info("Đã mở trình duyệt tới: {}", frontendUrl);
            } catch (Exception e) {
                log.error("Không thể mở trình duyệt tự động. Vui lòng truy cập: {}", frontendUrl, e);
            }
        } else {
            log.warn("Không thể tự động mở trình duyệt. Vui lòng truy cập thủ công vào: {}", frontendUrl);
        }
    }

    private boolean isDesktopSupported() {
        return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }
}