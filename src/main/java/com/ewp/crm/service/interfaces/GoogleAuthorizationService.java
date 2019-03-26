package com.ewp.crm.service.interfaces;

import com.google.api.client.auth.oauth2.Credential;

public interface GoogleAuthorizationService {

    String authorize();

    Credential tokenResponse(String code);

    Credential getCredential();
}
