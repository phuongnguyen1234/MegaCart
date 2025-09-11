package com.megacart;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BackendApplication {

	@PostConstruct
	public void init() {
		// Cài đặt múi giờ mặc định cho toàn bộ ứng dụng là UTC.
		// Việc này đảm bảo tính nhất quán của dữ liệu thời gian trên server và CSDL.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

    public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		// mvn clean install -DskipTests
		// java -jar target/backend-0.0.1-SNAPSHOT.jar
		// java "-Dspring.profiles.active=dev" -jar target/backend-0.0.1-SNAPSHOT.jar
	}
}
