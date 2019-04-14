package com.ewp.crm.service.interfaces;

import com.google.api.client.auth.oauth2.Credential;

public interface GoogleAuthorizationService {

    String authorize(GoogleTokenService.TokenType tokenType);

    Credential tokenResponse(String code, GoogleTokenService.TokenType tokenType);

    Credential getCredential(GoogleTokenService.TokenType tokenType);
}
