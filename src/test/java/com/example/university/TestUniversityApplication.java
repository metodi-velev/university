package com.example.university;

import org.springframework.boot.SpringApplication;

public class TestUniversityApplication {

	public static void main(String[] args) {
		SpringApplication.from(UniversityApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
