package com.empuje.web_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServiceApplication  {

	public static void main(String[] args) {
		System.out.println(System.getenv("DB_URL"));

		SpringApplication.run(WebServiceApplication .class, args);
	}

}
