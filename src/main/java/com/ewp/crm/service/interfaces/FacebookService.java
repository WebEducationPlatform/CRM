package com.ewp.crm.service.interfaces;

import com.ewp.crm.exceptions.util.FBAccessTokenException;

import java.util.Optional;

public interface FacebookService {

	Optional<Long> getFBIdByUrl(String url);

	void getFacebookMessages() throws FBAccessTokenException;
}

