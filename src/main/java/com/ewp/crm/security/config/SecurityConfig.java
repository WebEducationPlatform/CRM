package com.ewp.crm.security.config;

import com.ewp.crm.security.handlers.CustomAuthenticationSuccessHandler;
import com.ewp.crm.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final PersistentTokenRepository persistentTokenRepository;
    private final RequestMatcher csrfRequestMatcher = new RequestMatcher() {
        private final RegexRequestMatcher requestMatcher = new RegexRequestMatcher("/processing-url", null);

        @Override
        public boolean matches(HttpServletRequest request) {
            return requestMatcher.matches(request);
        }
    };

    @Autowired
    public SecurityConfig(AuthenticationService authenticationService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                          PersistentTokenRepository persistentTokenRepository) {
        this.authenticationService = authenticationService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.persistentTokenRepository = persistentTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/init/**", "/contract/**").permitAll()
                .antMatchers("/client/**").hasAnyAuthority("ADMIN", "USER", "OWNER", "MENTOR")
                .antMatchers("/admin/**").hasAnyAuthority("ADMIN", "OWNER", "MENTOR")
                .antMatchers("/student/**").hasAnyAuthority("ADMIN", "OWNER")
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/processing-url")
                .successHandler(customAuthenticationSuccessHandler)
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .exceptionHandling().accessDeniedPage("/accessDenied")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .permitAll().and()
                .csrf().requireCsrfProtectionMatcher(csrfRequestMatcher);

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);

        http
                .rememberMe()
                .tokenRepository(persistentTokenRepository)
                .rememberMeParameter("remember-me-param")
                .rememberMeCookieName("my-remember-me")
                .tokenValiditySeconds(604800);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Value("${project.password.encoder.strength}")
    private int strength;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(strength);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticationService);
    }
}
