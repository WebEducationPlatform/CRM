package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;

import java.util.List;

//TODO What class use for UTC format date
public interface SMSService {
	String sendSMS(Client client, String text);
	String sendSMS(List<Client> clients, String text);
	String scheduledSMS(Client client, String text, Object schedule);
	String scheduledSMS(List<Client> client, String text, Object schedule);
	String getBalance();
}
