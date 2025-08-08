package com.megacart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // Annotation này đã bao gồm ComponentScan, EntityScan, và EnableJpaRepositories
@EnableScheduling // Bật tính năng lập lịch
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

		// mvn clean install -DskipTests
		// java -jar target/backend-0.0.1-SNAPSHOT.jar
	}
}
