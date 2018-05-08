package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.SMSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:sms.properties")
public class SMSConfigImpl implements SMSConfig {

	private final String login;
	private final String password;
	private final String alphaName;

	@Autowired
	public SMSConfigImpl(Environment env) {
		this.login = env.getProperty("sms.login");
		this.password = env.getProperty("sms.password");
		this.alphaName = env.getProperty("sms.alphaName");
	}

	@Override
	public String getLogin() {
		return login;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getAlphaName() {
		return alphaName;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
