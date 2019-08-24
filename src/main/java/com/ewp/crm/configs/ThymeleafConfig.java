package com.ewp.crm.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ThymeleafConfig {

	private final static String PREFIX = "MessageTemplateText:";

	@Bean
	public TemplateResolver springThymeleafTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setCacheable(false);
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setCharacterEncoding("UTF-8");
		resolver.setTemplateMode("LEGACYHTML5");
		resolver.setCacheable(false);
		return resolver;
	}

	@Bean
	public StringTemplateResolver stringTemplateResolver() {
		StringTemplateResolver resolver = new StringTemplateResolver(PREFIX);
		resolver.setTemplateMode("LEGACYHTML5");
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public SpringTemplateEngine thymeleafTemplateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.addDialect(new SpringSecurityDialect());
		engine.addDialect(new Java8TimeDialect());
		Set<TemplateResolver> templateResolverSet = new HashSet<>();
		templateResolverSet.add(springThymeleafTemplateResolver());
		templateResolverSet.add(stringTemplateResolver());
		engine.setTemplateResolvers(templateResolverSet);
		return engine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(thymeleafTemplateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}
}
