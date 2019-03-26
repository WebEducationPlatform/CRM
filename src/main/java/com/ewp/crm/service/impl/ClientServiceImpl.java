package com.ewp.crm.service.impl;


import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.*;
import com.ewp.crm.utils.validators.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl extends CommonServiceImpl<Client> implements ClientService {

    private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

	private final String REPEATED_CLIENT = "Клиент оставлил повторную заявку";

	private final ClientRepository clientRepository;

    private StatusService statusService;

    private SendNotificationService sendNotificationService;

    private final SocialProfileService socialProfileService;
  
    private final RoleService roleService;

    private final VKService vkService;

    private final PhoneValidator phoneValidator;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, SocialProfileService socialProfileService,PhoneValidator phoneValidator, RoleService roleService, @Lazy VKService vkService) {
        this.clientRepository = clientRepository;
        this.socialProfileService = socialProfileService;
        this.vkService = vkService;
        this.roleService = roleService;
        this.phoneValidator = phoneValidator;
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
    public Optional<Client> getClientBySkype(String skypeLogin) {
        return Optional.ofNullable(clientRepository.getClientBySkype(skypeLogin));
    }

    @Override
    public List<Client> getClientsByOwnerUser(User ownerUser) {
        return clientRepository.getClientsByOwnerUser(ownerUser);
    }

    @Override
    public Optional<Client> getClientByEmail(String email) {
        return Optional.ofNullable(clientRepository.getClientByEmail(email));
    }

    @Override
    public Optional<Client> getClientByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(clientRepository.getClientByPhoneNumber(phoneNumber));
    }

    @Override
    public Optional<Client> getClientBySocialProfile(SocialProfile socialProfile) {
        List<SocialProfile> socialProfiles = new ArrayList<>();
        socialProfiles.add(socialProfile);
        return Optional.ofNullable(clientRepository.getClientBySocialProfiles(socialProfiles));
    }

    @Override
    public Optional<Client> getClientByID(Long id) {
        return clientRepository.findById(id);
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

        Optional<Client> existClient = Optional.empty();

        client.setPhoneNumber(phoneValidator.phoneRestore(client.getPhoneNumber()));

        if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
            client.setCanCall(true);
            existClient = Optional.ofNullable(clientRepository.getClientByPhoneNumber(client.getPhoneNumber()));

        }

        if (!existClient.isPresent() && client.getEmail() != null && !client.getEmail().isEmpty()) {
            existClient = Optional.ofNullable(clientRepository.getClientByEmail(client.getEmail()));

        }

        checkSocialIds(client);

        for (SocialProfile socialProfile : client.getSocialProfiles()) {
            if (!existClient.isPresent()) {
                socialProfile = socialProfileService.getSocialProfileBySocialIdAndSocialType(socialProfile.getSocialId(), socialProfile.getSocialProfileType().getName());
                if (socialProfile != null) {
                    existClient = getClientBySocialProfile(socialProfile);
                }
            } else {
                break;
            }
        }

        if (existClient.isPresent()) {
            //если с новым клиентом пришла история, то добавим ее к старому клиенту.
            for (ClientHistory clientHistory : client.getHistory()) {
                if (clientHistory.getTitle().contains("Новая заявка")) {
                    String repeated = clientHistory.getTitle().replaceAll("Новая заявка", "Повторная заявка");
                    clientHistory.setTitle(repeated);
                }
                existClient.get().addHistory(clientHistory);
            }

            existClient.get().setClientDescriptionComment(REPEATED_CLIENT);
            existClient.get().setRepeated(true);
            sendNotificationService.sendNotificationsAllUsers(existClient.get());
            statusService.getRepeatedStatusForClient().ifPresent(existClient.get()::setStatus);
            clientRepository.saveAndFlush(existClient.get());

            return;
        }

        clientRepository.saveAndFlush(client);
        sendNotificationService.sendNotificationsAllUsers(client);
    }

    private void checkSocialIds(Client client) {
        for (Iterator<SocialProfile> iterator = client.getSocialProfiles().iterator(); iterator.hasNext();) {
            SocialProfile socialProfile = iterator.next();
            if ("vk".equals(socialProfile.getSocialProfileType().getName()) && socialProfile.getSocialId().contains("vk")) {
                Optional<Long> id = vkService.getVKIdByUrl(socialProfile.getSocialId());
                if (id.isPresent()) {
                    socialProfile.setSocialId(String.valueOf(id.get()));
                } else {
                    client.setComment("Не удалось получить социальную сеть клиента: " + socialProfile.getSocialId() + "\n" + client.getComment());
                    client.deleteSocialProfile(socialProfile);
                }
            }
        }
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

        client.setPhoneNumber(phoneValidator.phoneRestore(client.getPhoneNumber()));

        if (client.getPhoneNumber() != null && !client.getPhoneNumber().isEmpty()) {
            client.setCanCall(true);
            Client clientByPhone = clientRepository.getClientByPhoneNumber(client.getPhoneNumber());
            if (clientByPhone != null && !client.getPhoneNumber().isEmpty() && !clientByPhone.getId().equals(client.getId())) {
                throw new ClientExistsException();
            }
        } else {
            client.setCanCall(false);
        }
        //checkSocialLinks(client);
        clientRepository.saveAndFlush(client);
    }

//    private void checkSocialLinks(Client client) {
//        for (int i = 0; i < client.getSocialProfiles().size(); i++) {
//            String link = client.getSocialProfiles().get(i).getSocialId();
//            SocialProfileType type = client.getSocialProfiles().get(i).getSocialProfileType();
//            if (type.getName().equals("unknown")) {
//                if (!link.startsWith("https")) {
//                    if (link.startsWith("http")) {
//                        link = link.replaceFirst("http", "https");
//                    } else {
//                        link = "https://" + link;
//                    }
//                }
//            } else {
//                int indexOfLastSlash = link.lastIndexOf("/");
//                if (indexOfLastSlash != -1) {
//                    link = link.substring(indexOfLastSlash + 1);
//                }
//                link = "https://" + type.getName() + ".com/" + link;
//            }
//            client.getSocialProfiles().get(i).setSocialId(link);
//        }
//    }

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
	public List<Client> getOrderedClientsInStatus(Status status, SortingType order, User user) {
        List<Client> orderedClients;
        boolean isAdmin = user.getRole().contains(roleService.getRoleByName("ADMIN")) || user.getRole().contains(roleService.getRoleByName("OWNER"));
		if (SortingType.NEW_FIRST.equals(order) || SortingType.OLD_FIRST.equals(order)) {
			orderedClients = clientRepository.getClientsInStatusOrderedByRegistration(status, order, isAdmin, user);
			return orderedClients;
		}
		if (SortingType.NEW_CHANGES_FIRST.equals(order) || SortingType.OLD_CHANGES_FIRST.equals(order)) {
			orderedClients = clientRepository.getClientsInStatusOrderedByHistory(status, order, isAdmin, user);
			return orderedClients;
		}
		logger.error("Error with sorting clients");
		return new ArrayList<>();
	}

    @Override
    public Optional<Client> findByNameAndLastNameIgnoreCase(String name, String lastName) {
        return Optional.ofNullable(clientRepository.getClientByNameAndLastNameIgnoreCase(name, lastName));
    }

}
