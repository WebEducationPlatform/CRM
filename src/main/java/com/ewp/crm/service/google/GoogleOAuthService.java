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
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
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

    public OAuth20Service oAuth20Service(boolean isRegister) {
        return new ServiceBuilder(env.getProperty("google-oauth.apiKey"))
                .apiSecret(env.getProperty("google-oauth.apiSecret"))
                .defaultScope(env.getProperty("google-oauth.scopes"))
                .callback(isRegister ? env.getProperty("google-oauth.callbackregister") : env.getProperty("google-oauth.callback"))
                .build(GoogleApi20.instance());
    }

    public boolean GoogleOAuth2(String code) throws IOException, InterruptedException, ExecutionException {
        OAuth20Service oAuth20Service = oAuth20Service(false);
        OAuth2AccessToken accessToken = oAuth20Service.getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v3/userinfo");
        oAuth20Service.signRequest(accessToken, request);
        Response response = oAuth20Service.execute(request);
        if (response.getCode() == 200) {
            Gson gson = new Gson();
            GoogleUserDTO person = gson.fromJson(response.getBody(), GoogleUserDTO.class);
            Optional<User> userFromGoogle = userService.getUserByEmail(person.getEmail());
           if (userFromGoogle.isPresent() && userFromGoogle.get().isEnabled()  ) {
               User user = userFromGoogle.get();
               UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(user,
                       user.getRole(), user.getAuthorities());
               SecurityContext sc = SecurityContextHolder.getContext();
               sc.setAuthentication(authReq);
               return true;
            }

        }
            return false;
    }

    public User getGoogleUserDTO (String code ) throws IOException, InterruptedException, ExecutionException {
        OAuth20Service oAuth20Service = oAuth20Service(true);
        OAuth2AccessToken accessToken = oAuth20Service.getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v3/userinfo");
        oAuth20Service.signRequest(accessToken, request);
        Response response = oAuth20Service.execute(request);
        if (response.getCode() == 200) {
            Gson gson = new Gson();
            GoogleUserDTO googleUserDTO = gson.fromJson(response.getBody(), GoogleUserDTO.class);
            Optional<User> userFromGoogle = userService.getUserByEmail(googleUserDTO.getEmail());
            if (!userFromGoogle.isPresent()  ) {
                User newUser = new User();
                newUser.setEmail(googleUserDTO.getEmail());
                newUser.setFirstName(googleUserDTO.getGiven_name());
                newUser.setLastName(googleUserDTO.getFamily_name());
                newUser.setPassword(googleUserDTO.getEmail());
                newUser.setCity("");
                newUser.setCountry("");
                newUser.setPhoneNumber("");
                newUser.setSex("");
                return userService.add(newUser);
            }

        }
            return null;
    }


}
