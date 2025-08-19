package com.megacart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BackendApplication {
    public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		// mvn clean install -DskipTests
		// java -jar target/backend-0.0.1-SNAPSHOT.jar
	}
}
