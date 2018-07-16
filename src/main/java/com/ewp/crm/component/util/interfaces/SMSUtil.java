package com.ewp.crm.component.util.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import org.json.JSONException;

import java.util.List;

public interface SMSUtil {
	void sendSMS(Long clientId, Long templateId, String body) throws JSONException;
	void schedulerSendSMS(Long clientId, Long templateId) throws JSONException;
	void sendSMS(List<Client> clients, String text, User sender);
	void plannedSMS(Client client, String text, String date, User sender);
	void plannedSMS(List<Client> client, String text, String date, User sender);
	String getBalance();
	String getStatusMessage(long smsId);
}
