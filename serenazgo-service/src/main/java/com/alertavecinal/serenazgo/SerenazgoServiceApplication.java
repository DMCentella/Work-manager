package com.alertavecinal.serenazgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SerenazgoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerenazgoServiceApplication.class, args);
	}

}
