package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.AssignSkypeCall;

import java.util.List;

public interface AssignSkypeCallService extends CommonService<AssignSkypeCall> {

	void addSkypeCall(AssignSkypeCall assignSkypeCall);

	List<AssignSkypeCall> getSkypeCallDate();
}
