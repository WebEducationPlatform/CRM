package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.FacebookMessage;
import java.time.LocalDateTime;
import java.util.List;

public interface FacebookMessageService extends CommonService<FacebookMessage> {

	LocalDateTime findMaxDate();

	FacebookMessage addFacebookMessage(FacebookMessage facebookMessage);

	void addBatchMessages(List<FacebookMessage> clients);
}
