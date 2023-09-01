package com.openapi.mocker;

import com.openapi.mocker.parser.OpenAPIParserComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.openapi.mocker")
public class MockerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockerApplication.class, args);
	}

}
