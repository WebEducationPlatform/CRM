package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.User;
import org.json.JSONException;

import java.util.List;
import java.util.Set;

public interface SMSService {

	void sendSMS(Set<ClientData> phoneNumbers, String text);

	void sendSMS(Long clientId, String templateId, String body, User principal) throws JSONException;

	/**
	 * Send SMS notification to client without logging and additional body parameters.
	 * @param clientId recipient client.
	 * @param templateText email template text.
	 */
	void sendSimpleSMS(Long clientId, String templateText);

	void sendSMS(List<Client> clients, String text, User sender);

	void plannedSMS(Client client, String text, String date, User sender);

	void plannedSMS(List<Client> client, String text, String date, User sender);

	String getBalance();

	String getStatusMessage(long smsId);
}
