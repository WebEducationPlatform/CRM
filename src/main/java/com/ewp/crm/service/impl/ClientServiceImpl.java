package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SendNotificationService;
import com.ewp.crm.service.interfaces.StatusService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClientServiceImpl extends CommonServiceImpl<Client> implements ClientService {

	private final ClientRepository clientRepository;

	private StatusService statusService;

	private SendNotificationService sendNotificationService;

	@Autowired
	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Autowired
	RestTemplate restTemplate;

	@Override
	public List<Client> getAllClientsByStatus(Status status) {
		return clientRepository.getAllByStatus(status);
	}

	@Override
	public List<Client> getAllClients() {
		return clientRepository.findAll();
	}

	@Override
	public Client getClientBySkype(String skypeLogin) {
		return clientRepository.getClientBySkype(skypeLogin);
	}

	@Override
	public List<Client> getClientsByOwnerUser(User ownerUser) {
		return clientRepository.getClientsByOwnerUser(ownerUser);
	}

	@Override
	public Client getClientByEmail(String email) {
		return clientRepository.getClientByEmail(email);
	}

	@Override
	public Client getClientByPhoneNumber(String phoneNumber) {
		return clientRepository.getClientByPhoneNumber(phoneNumber);
	}

	@Override
	public Client getClientBySocialProfile(SocialProfile socialProfile) {
		List<SocialProfile> socialProfiles = new ArrayList<>();
		socialProfiles.add(socialProfile);
		return clientRepository.getClientBySocialProfiles(socialProfiles);
	}

	@Override
	public Client getClientByID(Long id) {
		Optional<Client> optional = clientRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			return null;
		}
	}

	@Override
	public List<Client> filteringClient(FilteringCondition filteringCondition) {
		return clientRepository.filteringClient(filteringCondition);
	}

	@Override
	public List<Client> getChangeActiveClients() {
		return clientRepository.getChangeActiveClients();
	}

	@Override
	public List<Client> getClientsByManyIds(List<Long> ids) {
		return clientRepository.getById(ids);
	}

	@Override
	public void updateBatchClients(List<Client> clients) {
		clientRepository.updateBatchClients(clients);
	}

	@Override
	public void addBatchClients(List<Client> clients) {
		clientRepository.addBatchClients(clients);
	}

	@Override
	public void addClient(Client client) {
		if (client.getLastName() == null) {
			client.setLastName("");
		}
		checkSocialLinks(client);
		Status firstStatus = statusService.getFirstStatusForClient();
		if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
			phoneNumberValidation(client);
			Client clientByPhone = clientRepository.getClientByPhoneNumber(client.getPhoneNumber());
			if (clientByPhone != null) {
				clientByPhone.setStatus(firstStatus);
				clientRepository.saveAndFlush(clientByPhone);
				sendNotificationService.sendNotificationsAllUsers(clientByPhone);
				return;
			}
		}
		if (client.getEmail() != null && !client.getEmail().isEmpty()) {
			Client clientByEmail = clientRepository.getClientByEmail(client.getEmail());
			if (clientByEmail != null) {
				clientByEmail.setStatus(firstStatus);
				clientRepository.saveAndFlush(clientByEmail);
				sendNotificationService.sendNotificationsAllUsers(clientByEmail);
				return;
			}
		}
		clientRepository.saveAndFlush(client);
		sendNotificationService.sendNotificationsAllUsers(client);
	}

	@Override
	public List<String> getClientsEmails() {
		return clientRepository.getClientsEmail();
	}

	@Override
	public List<String> getClientsPhoneNumbers() {
		return clientRepository.getClientsPhoneNumber();
	}

	@Override
	public List<String> getFilteredClientsEmail(FilteringCondition filteringCondition) {
		return clientRepository.getFilteredClientsEmail(filteringCondition);
	}

	@Override
	public List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition) {
		return clientRepository.getFilteredClientsPhoneNumber(filteringCondition);
	}

	@Override
	public List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition) {
		return clientRepository.getFilteredClientsSNLinks(filteringCondition);
	}

	@Override
	public List<Client> getClientsByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser) {
		return clientRepository.getByStatusAndOwnerUserOrOwnerUserIsNull(status, ownUser);
	}

	@Override
	public List<Client> getAllClientsByPage(Pageable pageable) {
		return clientRepository.findAll(pageable).getContent();
	}

	@Override
	public void updateClient(Client client) {
		if (client.getEmail() != null && !client.getEmail().isEmpty()) {
			Client clientByMail = clientRepository.getClientByEmail(client.getEmail());
			if (clientByMail != null && !clientByMail.getId().equals(client.getId())) {
				throw new ClientExistsException();
			}
		}
		if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
			phoneNumberValidation(client);
			Client clientByPhone = clientRepository.getClientByPhoneNumber(client.getPhoneNumber());
			if (clientByPhone != null && !clientByPhone.getId().equals(client.getId())) {
				throw new ClientExistsException();
			}
		}
		checkSocialLinks(client);
		clientRepository.saveAndFlush(client);
	}

	private void phoneNumberValidation(Client client) {
		String phoneNumber = client.getPhoneNumber();
		Pattern pattern = Pattern.compile("^((8|\\+7|7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
		Matcher matcher = pattern.matcher(phoneNumber);
		if (matcher.matches()) {
			client.setCanCall(true);
			if (phoneNumber.startsWith("8")) {
				phoneNumber = phoneNumber.replaceFirst("8", "7");
			}
			client.setPhoneNumber(phoneNumber.replaceAll("[+()-]", "")
					.replaceAll("\\s", ""));
		} else {
			client.setCanCall(false);
		}
	}

	private void checkSocialLinks(Client client) {
		for (int i = 0; i < client.getSocialProfiles().size(); i++) {
			long link1 = client.getSocialProfiles().get(i).getSocialNetworkId();
			String link = String.valueOf(link1);
			SocialProfileType type = client.getSocialProfiles().get(i).getSocialProfileType();
			if(type.getName().equals("vk")) {
//				link = client.getSocialProfiles().get(i).getSocialNetworkId();
				if(!link.matches("[-+]?\\d+") && !link.equals("https://vk.com/id")){
					String idVK = restTemplate.getForObject("https://api.vk.com/method/users.get?access_token=beb3e3ed96e19e2401868a11e7f68e69213377d8cce91eb1fe81ab7fc1bb39ec9fd94f99d45b11d11c09d&v=5.78&user_ids="+link,String.class);
					try {
						JSONArray response = new JSONObject(idVK).getJSONArray("response");
						JSONObject jo = response.getJSONObject(0);
						idVK = jo.getString("id");

					} catch (JSONException e) {
						e.printStackTrace();
					}
//					client.getSocialProfiles().get(i).setSocialNetworkId(idVK);
                }

			} else if(type.getName().equals("facebook")) {
//				link = client.getSocialProfiles().get(i).getSocialNetworkId();
				if(!link.matches("[-+]?\\d+") && !link.equals("https://fb.com/id")) {

					String idFB = restTemplate.getForObject("https://graph.facebook.com/v3.2/https://cocacola?access_token=EAAEeyqp8Ft0BANiIiZAQ0nJu3vZBm1xNpFGZBIwVS5SqL4gQt15erJY5PtytEd0y3YnsGGbotEg9fwlzQZBUXdnIkkKRhkUjZB5ymVncFZCv3VZBLcKv2fQbJKOfArH6ZCGWFyjHahsJq0zmD5tQiVfbEa9FywM9U5OrYwwOMs2YsQaH58yKQDJm",String.class);

//					String jSon = restTemplate.getForObject("https://graph.facebook.com/v3.2/"+link+"?fields=id&access_token=EAAIZBdSihu1cBABH4jF0ueLU8iIlrDBLi78n0ksB1LgSgcGWwvVRSZBZAdFveAU26M5sN75oh3gFyhOwDZA590DA0mteq9TWotLQcaS44b3vVHZAQvZA3f7p9lRm58pcibEsNifUCFZAN1Et2a885u3nEYYCjozwCXc3sLMQBkzZCwZDZD",String.class);
//					JSONObject jSon = restTemplate.getForObject("https://graph.facebook.com/v3.2/"+link+"+?fields=id&access_token=EAAEeyqp8Ft0BANiIiZAQ0nJu3vZBm1xNpFGZBIwVS5SqL4gQt15erJY5PtytEd0y3YnsGGbotEg9fwlzQZBUXdnIkkKRhkUjZB5ymVncFZCv3VZBLcKv2fQbJKOfArH6ZCGWFyjHahsJq0zmD5tQiVfbEa9FywM9U5OrYwwOMs2YsQaH58yKQDJm&format=json",JSONObject.class);
//					 link = restTemplate.getForObject( "https://graph.facebook.com/v3.2/me?fields=id&access_token=EAAEeyqp8Ft0BANiIiZAQ0nJu3vZBm1xNpFGZBIwVS5SqL4gQt15erJY5PtytEd0y3YnsGGbotEg9fwlzQZBUXdnIkkKRhkUjZB5ymVncFZCv3VZBLcKv2fQbJKOfArH6ZCGWFyjHahsJq0zmD5tQiVfbEa9FywM9U5OrYwwOMs2YsQaH58yKQDJm", String.class);

					try {

                        JSONObject jo = new JSONObject(link);
						link = jo.get("id").toString();

					} catch (JSONException e) {
						e.printStackTrace();
					}

//					client.getSocialProfiles().get(i).setSocialNetworkId(idFB);
				}

				} else {
					int indexOfLastSlash = link.lastIndexOf("/");
					if (indexOfLastSlash != -1) {
					link = link.substring(indexOfLastSlash + 1);
					}
					link = "https://" + type.getName() + ".com/" + link;
//				    client.getSocialProfiles().get(i).setSocialNetworkId(link);
			}


		}
	}

	@Override
	public List<Client> getClientsBySearchPhrase(String search) {
		return clientRepository.getClientsBySearchPhrase(search);
	}

	@Autowired
	public void setSendNotificationService(SendNotificationService sendNotificationService) {
		this.sendNotificationService = sendNotificationService;
	}

	@Autowired
	private void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}
}
