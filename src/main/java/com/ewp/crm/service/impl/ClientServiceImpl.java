package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClientServiceImpl extends CommonServiceImpl<Client> implements ClientService {

    private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

	private final String REPEATED_CLIENT = "Клиент оставлил повторную заявку";

	private final ClientRepository clientRepository;

    private StatusService statusService;

    private SendNotificationService sendNotificationService;

    private final SocialProfileService socialProfileService;

    private final VKService vkService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, SocialProfileService socialProfileService, @Lazy VKService vkService) {
        this.clientRepository = clientRepository;
        this.socialProfileService = socialProfileService;
        this.vkService = vkService;
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

        Client existClient = null;

        if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
            phoneNumberValidation(client);
            existClient = clientRepository.getClientByPhoneNumber(client.getPhoneNumber());

        }

        if (existClient == null && client.getEmail() != null && !client.getEmail().isEmpty()) {
            existClient = clientRepository.getClientByEmail(client.getEmail());

        }

        checkSocialIds(client);

        for (SocialProfile socialProfile : client.getSocialProfiles()) {
            if (existClient == null) {
                socialProfile = socialProfileService.getSocialProfileBySocialIdAndSocialType(socialProfile.getSocialId(), socialProfile.getSocialProfileType().getName());
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
                if (clientHistory.getTitle().contains("Новая заявка")) {
                    String repeated = clientHistory.getTitle().replaceAll("Новая заявка", "Повторная заявка");
                    clientHistory.setTitle(repeated);
                }
                existClient.addHistory(clientHistory);
            }

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

    private void checkSocialIds(Client client) {
        List<SocialProfile> profiles = new ArrayList<>();
        for (SocialProfile socialProfile :client.getSocialProfiles()) {
            if ("vk".equals(socialProfile.getSocialProfileType().getName()) && socialProfile.getSocialId().contains("vk")) {
                Optional<Long> id = vkService.getVKIdByUrl(socialProfile.getSocialId());
                if (id.isPresent()) {
                    socialProfile.setSocialId(String.valueOf(id.get()));
                    profiles.add(socialProfile);
                }
            } else {
                profiles.add(socialProfile);
            }
        }
        client.setSocialProfiles(profiles);
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
    public List<Client> getClientsByStatusAndOwnerUserOrOwnerUserIsNull(Status status, User ownUser, SortingType order) {
        List<Client> orderedClients;
        if (SortingType.NEW_FIRST.equals(order) || SortingType.OLD_FIRST.equals(order)) {
            orderedClients = clientRepository.getByStatusAndOwnerUserOrOwnerUserIsNullOrderedByRegistration(status, ownUser, order);
            return orderedClients;
        }
        if (SortingType.NEW_CHANGES_FIRST.equals(order) || SortingType.OLD_CHANGES_FIRST.equals(order)) {
            orderedClients = clientRepository.getByStatusAndOwnerUserOrOwnerUserIsNullOrderedByHistory(status, ownUser, order);
            return orderedClients;
        }
        logger.error("Error with sorting clients");
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

        checkSocialIds(client);

        if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
            phoneNumberValidation(client);
            Client clientByPhone = clientRepository.getClientByPhoneNumber(client.getPhoneNumber());
            if (clientByPhone != null && !clientByPhone.getId().equals(client.getId())) {
                throw new ClientExistsException();
            }
        }
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

	@Override
	public List<Client> getOrderedClientsInStatus(Status status, SortingType order) {
		List<Client> orderedClients;
		if (SortingType.NEW_FIRST.equals(order) || SortingType.OLD_FIRST.equals(order)) {
			orderedClients = clientRepository.getClientsInStatusOrderedByRegistration(status, order);
			return orderedClients;
		}
		if (SortingType.NEW_CHANGES_FIRST.equals(order) || SortingType.OLD_CHANGES_FIRST.equals(order)) {
			orderedClients = clientRepository.getClientsInStatusOrderedByHistory(status, order);
			return orderedClients;
		}
		logger.error("Error with sorting clients");
		return new ArrayList<>();
	}

    @Override
    public Client findByNameAndLastNameIgnoreCase(String name, String lastName) {
        return clientRepository.getClientByNameAndLastNameIgnoreCase(name, lastName);
    }

    // TODO Удалить после первого использования
    @Override
    public void refactorDataBase() {
        getAll().forEach(c -> c.getSocialProfiles().forEach(p -> {
                    if (p.getSocialId().contains("vk") && "vk".equals(p.getSocialProfileType().getName())) {
                        Optional<Long> id = vkService.getVKIdByUrl(p.getSocialId());
                        if (id.isPresent()) {
                            p.setSocialId(String.valueOf(id.get()));
                            updateClient(c);
                        }
                    }
                }
        ));
    }
}
