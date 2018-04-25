package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;

import java.util.List;

public interface SMSService {
	String sendSMS(Client client, String text);
	String sendSMS(List<Client> clients, String text);
	String scheduledSMS(Client client, String text, String date);
	String scheduledSMS(List<Client> client, String text, String date);
	String getBalance();
}
