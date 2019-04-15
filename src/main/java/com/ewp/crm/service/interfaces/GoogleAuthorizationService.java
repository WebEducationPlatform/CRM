package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.GoogleToken;
import com.google.api.client.auth.oauth2.Credential;

public interface GoogleAuthorizationService {

    String authorize(GoogleToken.TokenType tokenType);

    Credential tokenResponse(String code, GoogleToken.TokenType tokenType);

    Credential getCredential(GoogleToken.TokenType tokenType);
}
