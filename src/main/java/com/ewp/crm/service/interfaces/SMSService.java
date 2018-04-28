package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import org.joda.time.DateTime;

import java.util.List;

public interface SMSService {
	String sendSMS(Client client, String text);
	String sendSMS(List<Client> clients, String text);
	String plannedSMS(Client client, String text, String date);
	String plannedSMS(List<Client> client, String text, String date);
	String getBalance();
}
