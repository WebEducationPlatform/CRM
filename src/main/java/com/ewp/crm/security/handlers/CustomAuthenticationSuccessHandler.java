package com.ewp.crm.security.handlers;


import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@Service
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private RequestCache requestCache = new HttpSessionRequestCache();

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if (savedRequest == null) {
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        } else {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request,response,targetUrl);
            clearAuthenticationAttributes(request);
        }
    }

    protected void handle(HttpServletRequest request,
                          HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authorities.contains(new Role("OWNER"))) {
            logger.info(user.getEmail() + " has been logged in like OWNER");
            return "/client";
        }
        if (authorities.contains(new Role("ADMIN"))) {
            logger.info(user.getEmail() + " has been logged in like ADMIN");
            return "/client";
        }
        if (authorities.contains(new Role("USER"))) {
            logger.info(user.getEmail() + " has been logged in like USER");
            return "/client";
        }
        if (authorities.contains(new Role("MENTOR"))) {
            logger.info(user.getEmail() + " has been logged in like MENTOR");
            return "/client";
        }
        if (authorities.contains(new Role("HR"))) {
            logger.info(user.getEmail() + " has been logged in like HR");
            return "/client";
        }
        throw new IllegalStateException();
    }
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}