package com.ewp.crm;

import com.ewp.crm.configs.initializer.DataInitializer;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.io.IOException;

@SpringBootApplication
public class CrmApplication {

	public static void main(String[] args) throws IOException, JSONException, InterruptedException {
		SpringApplication.run(CrmApplication.class, args);
    }

	@Bean(initMethod = "init")
	public DataInitializer initTestData() {
		return new DataInitializer();
	}
}


