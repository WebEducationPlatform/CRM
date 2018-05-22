package com.ewp.crm;

import com.ewp.crm.configs.initializer.DataInitializer;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

	@Bean(initMethod = "init")
	public DataInitializer initTestData() {
		return new DataInitializer();
	}

	@Bean
	public Javers getJavers(){
		return JaversBuilder.javers().build();
	}
}
