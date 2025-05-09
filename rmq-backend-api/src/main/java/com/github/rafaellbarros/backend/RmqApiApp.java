package com.github.rafaellbarros.backend;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class RmqApiApp {

	public static void main(String[] args) {
		SpringApplication.run(RmqApiApp.class, args);
	}

}
