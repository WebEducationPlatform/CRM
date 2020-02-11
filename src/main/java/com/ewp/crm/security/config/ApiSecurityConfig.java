package com.ewp.crm.security.config;

/**
 * Доступ к api сделан на основе jwt.
 * Для того чтобы получить доступ к данным необходимо обратиться на /rest/api/login (см. AuthApiController)
 * и передать в теле запроса данные пользователя и пароль:
 * {
 * 	"email":"user_email@gmail.com",
 * 	"password":"123456"
 * }
 * В ответ придет токен доступа, которым необходимо подписать каждый запрос к /rest/api/**. В заголовок запроса
 * нужно вставить поле Authorization со значением Bearer_T (вместо Т подставить значение токена)
 */

import com.ewp.crm.security.jwt.JwtTokenFilter;
import com.ewp.crm.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public ApiSecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    protected void configure(HttpSecurity http) throws Exception {
        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);

        http
                .antMatcher("/rest/api/**")
                .authorizeRequests()
                    .antMatchers("/rest/api/login").permitAll()
                    .antMatchers("/rest/api/**").hasAnyAuthority("ADMIN", "OWNER")
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
