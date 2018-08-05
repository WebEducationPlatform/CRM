package com.ewp.crm.service.interfaces;

import com.ewp.crm.exceptions.util.FBAccessTokenException;
import com.ewp.crm.models.FacebookMessage;

import java.util.List;
import java.util.Optional;

public interface FacebookService {

	void getFacebookMessages() throws FBAccessTokenException;
}

