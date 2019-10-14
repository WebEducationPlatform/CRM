package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.AuthenticationRequestDto;
import com.ewp.crm.security.jwt.JwtTokenProvider;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest/api")
public class AuthApiController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthApiController(UserService userService, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto authReqDto) {

        String email = authReqDto.getEmail();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, authReqDto.getPassword()));
            User user = userService.getUserByEmail(email).get();

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + email + " not found");
            }

            String token = jwtTokenProvider.createToken(email, user.getRole());
            Map<Object, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
