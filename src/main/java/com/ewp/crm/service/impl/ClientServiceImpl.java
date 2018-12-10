package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SendNotificationService;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClientServiceImpl extends CommonServiceImpl<Client> implements ClientService {

	private final String REPEATED_CLIENT = "Клиент оставлил повторную заявку";

	private final ClientRepository clientRepository;

	private StatusService statusService;

	private SendNotificationService sendNotificationService;

	private final SocialProfileService socialProfileService;

	@Autowired
	public ClientServiceImpl(ClientRepository clientRepository, SocialProfileService socialProfileService) {
		this.clientRepository = clientRepository;
		this.socialProfileService = socialProfileService;
	}

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

        Client existClient = null;

        if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
            phoneNumberValidation(client);
            existClient = clientRepository.getClientByPhoneNumber(client.getPhoneNumber());

        }

        if (existClient == null && client.getEmail() != null && !client.getEmail().isEmpty()) {
            existClient = clientRepository.getClientByEmail(client.getEmail());

        }

        for (SocialProfile socialProfile : client.getSocialProfiles()) {
            if (existClient == null) {
                socialProfile = socialProfileService.getSocialProfileByLink(socialProfile.getLink());
                if (socialProfile != null) {
                    existClient = getClientBySocialProfile(socialProfile);
                }
            } else {
                break;
            }
        }

        if (existClient != null) {
            //если с новым клиентом пришла история, то добавим ее к старому клиенту.
            for (ClientHistory clientHistory : client.getHistory()) {
                existClient.addHistory(clientHistory);
            }

//            String currectDescription = existClient.getClientDescriptionComment();
            existClient.setClientDescriptionComment(REPEATED_CLIENT);
            existClient.setRepeated(true);
            sendNotificationService.sendNotificationsAllUsers(existClient);
            existClient.setStatus(statusService.getRepeatedStatusForClient());
            clientRepository.saveAndFlush(existClient);

            return;
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
		for (SocialProfile socialProfile: client.getSocialProfiles()) {
			String link = socialProfile.getLink();
			SocialProfileType type = socialProfile.getSocialProfileType();
			if (type.getName().equals("unknown")) {
				if (!link.startsWith("https")) {
					if (link.startsWith("http")) {
						link = link.replaceFirst("http", "https");
					} else {
						link = "https://" + link;
					}
				}
			} else {
				int indexOfLastSlash = link.lastIndexOf("/");
				if (indexOfLastSlash != -1) {
					link = link.substring(indexOfLastSlash + 1);
				}
				link = "https://" + type.getName() + ".com/" + link;
			}
			socialProfile.setLink(link);
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
