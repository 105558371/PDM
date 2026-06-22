package com.example.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.myproject.util.BCryptUtil;

@SpringBootApplication
@EnableScheduling
public class MyprojectApplication {

	public static void main(String[] args) {
		// Generate a correct BCrypt hash for password "123"
		String hash = BCryptUtil.hashPassword("123");
		System.out.println("COPY THIS HASH: " + hash);
		
		SpringApplication.run(MyprojectApplication.class, args);
	}
}