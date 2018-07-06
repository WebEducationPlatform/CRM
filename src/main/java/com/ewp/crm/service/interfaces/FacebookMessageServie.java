package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.FacebookMessage;
import com.ewp.crm.models.MessageDialog;

import java.util.List;

public interface FacebookMessageServie {

	FacebookMessage addFacebookMessage(FacebookMessage facebookMessage);

	List<FacebookMessage> getAllFacebookMessages();

	FacebookMessage getFacebookMessage(Long id);

	void deleteFacebookMessage(FacebookMessage messageDialog);

	void deleteFacebookMessage(Long id);

	void updateFacebookMessage(FacebookMessage messageDialog);
}
