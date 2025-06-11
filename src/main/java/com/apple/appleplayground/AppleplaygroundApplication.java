package com.apple.appleplayground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AppleplaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppleplaygroundApplication.class, args);
	}

}
