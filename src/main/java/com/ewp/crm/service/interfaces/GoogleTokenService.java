package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.GoogleToken;

import java.util.Optional;

public interface GoogleTokenService {

    Optional<GoogleToken> getToken(TokenType tokenType);

    void createOrUpdate(GoogleToken accessToken, TokenType tokenType);

    Optional<GoogleToken> getRefreshedToken(TokenType tokenType);

    enum TokenType {
        CALENDAR,
        DRIVE
    }

}
