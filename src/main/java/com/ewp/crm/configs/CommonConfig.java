package com.ewp.crm.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
public class CommonConfig {

	//TODO to application.properties
	@Bean
	public TemplateResolver springThymeleafTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setOrder(1);
		resolver.setCacheable(false);
		return resolver;
	}
}
