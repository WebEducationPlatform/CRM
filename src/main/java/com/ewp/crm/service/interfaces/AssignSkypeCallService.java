package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.AssignSkypeCall;

import java.util.List;
import java.util.Optional;

public interface AssignSkypeCallService extends CommonService<AssignSkypeCall> {

	void addSkypeCall(AssignSkypeCall assignSkypeCall);

	List<AssignSkypeCall> getAssignSkypeCallIfNotificationWasNoSent();

	List<AssignSkypeCall> getAssignSkypeCallIfCallDateHasAlreadyPassedButHasNotBeenClearedToTheClient();
	
	List<AssignSkypeCall> getAssignSkypeCallClientsWithoutMentors();
	
	void deleteByIdSkypeCall(Long id);

	Optional<AssignSkypeCall> getAssignSkypeCallByClientId(Long clientId);
}