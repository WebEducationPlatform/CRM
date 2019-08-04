package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.AssignSkypeCall;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignSkypeCallRepository extends CommonGenericRepository<AssignSkypeCall> {

	@Query(value = "select sl from AssignSkypeCall sl where now() >= sl.notificationBeforeOfSkypeCall and sl.theNotificationWasIsSent = false")
	List<AssignSkypeCall> getAssignSkypeCallIfNotificationWasNoSent();

	@Query(value = "select sl from AssignSkypeCall sl where now() >= sl.skypeCallDate and sl.skypeCallDateCompleted = false")
	List<AssignSkypeCall> getAssignSkypeCallIfCallDateHasAlreadyPassedButHasNotBeenClearedToTheClient();

	@Query(value = "select sl from AssignSkypeCall sl join sl.toAssignSkypeCall c where c.ownerMentor = null order by sl.skypeCallDate")
	List<AssignSkypeCall> getAssignSkypeCallClientsWithoutMentors();
	
	@Query(value = "select sl from AssignSkypeCall sl where sl.toAssignSkypeCall.id = ?1 and sl.skypeCallDateCompleted = false")
	AssignSkypeCall getAssignSkypeCallByClientId(Long clientId);
}