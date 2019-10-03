package com.ewp.crm.service.google;

import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.GoogleUserDTO;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@PropertySource("file:./google-oauth.properties" )
public class GoogleOAuthService {
    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;
    private Environment env;

    @Autowired
    public GoogleOAuthService(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, Environment env) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    public boolean GoogleOAuth2(String code) throws IOException, InterruptedException, ExecutionException {
        OAuth2AccessToken accessToken = oAuth20Service().getAccessToken(code);
//        get Google profile
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v3/userinfo");
        oAuth20Service().signRequest(accessToken, request);
        Response response = oAuth20Service().execute(request);
        if (response.getCode() == 200) {
            Gson gson = new Gson();
            GoogleUserDTO person = gson.fromJson(response.getBody(), GoogleUserDTO.class);
            User userFromGoogle = userService.getUserByEmail(person.getEmail()).get();
            UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userFromGoogle,
                    userFromGoogle.getRole(), userFromGoogle.getAuthorities());
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authReq);
            return true;
        } else {
            return false;
        }

    }

    public OAuth20Service oAuth20Service() {
        return new ServiceBuilder(env.getProperty("google-oauth.apiKey"))
                .apiSecret(env.getProperty("google-oauth.apiSecret"))
//                .responseType(env.getProperty("google-oauth.responseType"))
                .defaultScope(env.getProperty("google-oauth.scopes"))
                .callback(env.getProperty("google-oauth.callback"))
                .build(GoogleApi20.instance());
    }
}
