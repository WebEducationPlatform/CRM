package com.ewp.crm.service.interfaces;

public interface IPService {

	void call(String from, String to, Long callId);

	String getVoximplantLoginForWebCall();

	String getVoximplantPasswordForWebCall();
}
