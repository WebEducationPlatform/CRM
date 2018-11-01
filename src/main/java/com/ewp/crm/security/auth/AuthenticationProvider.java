package com.ewp.crm.security.auth;

import com.ewp.crm.models.User;
import com.ewp.crm.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private final AuthenticationService authenticationService;

    private final CustomPasswordEncoder customPasswordEncoder;

    @Autowired
    public AuthenticationProvider(AuthenticationService authenticationService, CustomPasswordEncoder customPasswordEncoder) {
        this.authenticationService = authenticationService;
        this.customPasswordEncoder = customPasswordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login    = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (login == null || login.isEmpty()) {
            throw new BadCredentialsException("Unable to authentication user without login.");
        }
        if (password == null || password.isEmpty()) {
            throw new BadCredentialsException("Unable to authentication user password.");
        }

        User user = (User) authenticationService.loadUserByUsername(login);

        String PasswordHashIn = customPasswordEncoder.encodePassword(password,user.getSalt());

        if (!customPasswordEncoder.matches(PasswordHashIn, user.getPassword())) {
            throw new BadCredentialsException("Wrong passworrd for: " + login);
        }

        return new UsernamePasswordAuthenticationToken(user, PasswordHashIn, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
