package com.ewp.crm.security.jwt;


import org.springframework.security.core.AuthenticationException;

public class JwtAuthException extends AuthenticationException {
    public JwtAuthException(String msg) {
        super(msg);
    }

    public JwtAuthException(String msg, Throwable t) {
        super(msg, t);
    }
}
