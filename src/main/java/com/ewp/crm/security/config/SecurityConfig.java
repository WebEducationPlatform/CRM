package com.ewp.crm.security.config;

import com.ewp.crm.security.auth.AuthenticationProvider;
import com.ewp.crm.security.auth.CustomPasswordEncoder;
import com.ewp.crm.security.filter.AjaxRequestHandlingFilter;
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
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final RequestMatcher csrfRequestMatcher = new RequestMatcher() {
        private final RegexRequestMatcher requestMatcher = new RegexRequestMatcher("/processing-url", null);

        @Override
        public boolean matches(HttpServletRequest request) {
            return requestMatcher.matches(request);
        }
    };

    private final AuthenticationProvider authenticationProvider;
    private final AjaxRequestHandlingFilter ajaxRequestHandlingFilter;

    @Autowired
    public SecurityConfig(AuthenticationService authenticationService
            ,CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler
            ,AuthenticationProvider authenticationProvider
            ,AjaxRequestHandlingFilter ajaxRequestHandlingFilter
    ) {
        this.authenticationService = authenticationService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.authenticationProvider = authenticationProvider;
        this.ajaxRequestHandlingFilter = ajaxRequestHandlingFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
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
                .permitAll().and()
                .csrf().requireCsrfProtectionMatcher(csrfRequestMatcher);


        http
                .sessionManagement()
                .maximumSessions(10000)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?logout")
                .sessionRegistry(sessionRegistry());

        http
                .addFilterAfter(ajaxRequestHandlingFilter, ExceptionTranslationFilter.class);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static CustomPasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(authenticationService)
                .passwordEncoder(passwordEncoder());
    }
}
