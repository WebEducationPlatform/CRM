package com.ewp.crm.service.interfaces;

import com.ewp.crm.exceptions.util.FBAccessTokenException;

import java.util.List;
import java.util.Optional;

public interface FacebookService {

	Optional<List<String>> getFacebookMessages() throws FBAccessTokenException;
}

