package com.megacart;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // Annotation này đã bao gồm ComponentScan, EntityScan, và EnableJpaRepositories
@EnableScheduling // Bật tính năng lập lịch
public class BackendApplication {
    private static boolean isFrontendReady(String url) {
    try {
			// Sử dụng URI.create(...).toURL() để tránh constructor bị deprecated của new URL(String)
			var connection = URI.create(url).toURL().openConnection();
			// Đặt timeout để tránh chờ đợi vô hạn nếu frontend không phản hồi
			connection.setConnectTimeout(1000); // 1 giây
			connection.setReadTimeout(1000);    // 1 giây
			connection.connect();
			return true;
		} catch (Exception e) {
			// Việc không kết nối được là bình thường khi đang chờ, không cần log lỗi
			return false;
		}
    }
    public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

        String frontendUrl = "http://localhost:5173";
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 20000) { // timeout 20s
            if (isFrontendReady(frontendUrl)) {
                try {
                    Desktop.getDesktop().browse(new URI(frontendUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
		// mvn clean install -DskipTests
		// java -jar target/backend-0.0.1-SNAPSHOT.jar
	}
}
	
