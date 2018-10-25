package com.ewp.crm.security.config;

import com.ewp.crm.security.handlers.CustomAuthenticationSuccessHandler;
import com.ewp.crm.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final AuthenticationService authenticationService;
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	public SecurityConfig(AuthenticationService authenticationService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
		this.authenticationService = authenticationService;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/client/**").hasAnyAuthority("ADMIN", "USER", "OWNER")
				.antMatchers("/admin/**").hasAnyAuthority("ADMIN", "OWNER")
				.antMatchers("/student/**").hasAnyAuthority("ADMIN", "OWNER")
				.and()
				.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/processing-url")
				.successHandler(customAuthenticationSuccessHandler)
				.usernameParameter("username")
				.passwordParameter("password")
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true)
				.permitAll();


		http
				.sessionManagement()
				.maximumSessions(10000)
				.maxSessionsPreventsLogin(false)
				.expiredUrl("/login?logout")
				.sessionRegistry(sessionRegistry());
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(authenticationService);
	}
}
