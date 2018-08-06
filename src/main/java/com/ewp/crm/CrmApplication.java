package com.ewp.crm;

import com.ewp.crm.configs.initializer.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootApplication
public class CrmApplication {
	@Autowired
	@Qualifier("thymeleafTemplateEngine")
	TemplateEngine templateEngine;


	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

	@Bean(initMethod = "init")
	public DataInitializer initTestData() {
		return new DataInitializer();
	}
}
