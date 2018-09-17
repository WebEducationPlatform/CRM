package com.ewp.crm.service.interfaces;

import com.ewp.crm.exceptions.util.FBAccessTokenException;

public interface FacebookService {

	void getFacebookMessages() throws FBAccessTokenException;
}

