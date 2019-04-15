package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.GoogleToken;

import java.util.Optional;

public interface GoogleTokenService {

    Optional<GoogleToken> getToken(GoogleToken.TokenType tokenType);

    void createOrUpdate(GoogleToken accessToken, GoogleToken.TokenType tokenType);

    Optional<GoogleToken> getRefreshedToken(GoogleToken.TokenType tokenType);

}
