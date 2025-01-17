package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com", "com.websocketchat"})
public class Tia104BookCartBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Tia104BookCartBackendApplication.class, args);
	}

}
