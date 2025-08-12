package com.hr_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SpringBootApplication
@RestControllerAdvice
public class HrAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrAnalyzerApplication.class, args);
	}

}
