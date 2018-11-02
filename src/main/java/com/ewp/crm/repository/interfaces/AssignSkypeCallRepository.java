package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.AssignSkypeCall;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AssignSkypeCallRepository extends CommonGenericRepository<AssignSkypeCall> {

	@Query(value = "select sl from AssignSkypeCall sl where now() >= sl.notificationBeforeOfSkypeCall and sl.theNotificationWasIsSent = false")
	List<AssignSkypeCall> getAssignSkypeCallIfNotificationWasNoSent();

	@Query(value = "select sl from AssignSkypeCall sl where now() >= sl.skypeCallDate and sl.skypeCallDateCompleted = false")
	List<AssignSkypeCall> getAssignSkypeCallIfCallDateHasAlreadyPassedButHasNotBeenClearedToTheClient();

	AssignSkypeCall getAssignSkypeCallBySkypeClientlogin(String skypeClientlogin);
}