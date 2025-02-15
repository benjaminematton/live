package com.example.live_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.live_backend.model")
public class LiveBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiveBackendApplication.class, args);
	}

}
