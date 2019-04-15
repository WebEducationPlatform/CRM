package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.GoogleToken;

public interface GoogleTokenRepository extends CommonGenericRepository<GoogleToken> {

    GoogleToken getByTokenType(GoogleToken.TokenType tokenType);
}
