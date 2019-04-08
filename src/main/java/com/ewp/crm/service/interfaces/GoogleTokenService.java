package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.GoogleToken;

import java.util.Optional;

public interface GoogleTokenService {

    Optional<GoogleToken> getToken();

    void createOrUpdate(GoogleToken accessToken);

    Optional<GoogleToken> getRefreshedToken();
}
