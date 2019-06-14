package com.ewp.crm.service.interfaces;

import com.ewp.crm.exceptions.util.FBAccessTokenException;

import java.util.Optional;

public interface FacebookService {

	void getFacebookMessages() throws FBAccessTokenException;

	Optional<String> getIdFromLink(String link);
}

