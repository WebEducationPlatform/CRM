package com.ewp.crm.service.impl;

import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.repository.SlackInviteLinkRepository;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.NotificationRepository;
import com.ewp.crm.service.interfaces.*;
import com.ewp.crm.util.validators.PhoneValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class ClientServiceImpl extends CommonServiceImpl<Client> implements ClientService {

    private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    private final ClientRepository clientRepository;
    private final SlackInviteLinkRepository slackInviteLinkRepository;
    private StatusService statusService;
    private SendNotificationService sendNotificationService;
    private NotificationRepository notificationRepository;
    private final SocialProfileService socialProfileService;
    private final ClientHistoryService clientHistoryService;
    private final RoleService roleService;
    private final VKService vkService;
    private final PhoneValidator phoneValidator;
    private final PassportService passportService;
    private final ProjectPropertiesService projectPropertiesService;
    private final SlackService slackService;
    private final ClientStatusChangingHistoryService clientStatusChangingHistoryService;
    private Environment env;
    private final UserService userService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, SocialProfileService socialProfileService,
                             ClientHistoryService clientHistoryService, PhoneValidator phoneValidator,
                             RoleService roleService, @Lazy VKService vkService, PassportService passportService,
                             ProjectPropertiesService projectPropertiesService, SlackInviteLinkRepository slackInviteLinkRepository,
                             NotificationRepository notificationRepository, @Lazy SlackService slackService, Environment env,
                             ClientStatusChangingHistoryService clientStatusChangingHistoryService, UserService userService) {
        this.clientRepository = clientRepository;
        this.socialProfileService = socialProfileService;
        this.clientHistoryService = clientHistoryService;
        this.vkService = vkService;
        this.roleService = roleService;
        this.phoneValidator = phoneValidator;
        this.passportService = passportService;
        this.slackInviteLinkRepository = slackInviteLinkRepository;
        this.notificationRepository = notificationRepository;
        this.projectPropertiesService = projectPropertiesService;
        this.slackService = slackService;
        this.env = env;
        this.clientStatusChangingHistoryService = clientStatusChangingHistoryService;
        this.userService = userService;
    }

    @Override
    public Optional<Client> getClientBySlackInviteHash(String hash) {
        if (slackInviteLinkRepository.existsByHash(hash)) {
            SlackInviteLink slackInviteLink = slackInviteLinkRepository.getByHash(hash);
            if (slackInviteLink != null) {
                return Optional.ofNullable(slackInviteLink.getClient());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean hasClientSocialProfileByType(Client client, String socialProfileType) {
        return clientRepository.hasClientSocialProfileByType(client, socialProfileType);
    }

    @Override
    public boolean inviteToSlack(Client client, String name, String lastName, String email) {
        if (!hasClientSocialProfileByType(client, "slack")) {
            if (name != null && lastName != null && email != null && !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {
                Client.Builder newClientBuilder = new Client.Builder(name, null, email);
                Client newClient = newClientBuilder.lastName(lastName).build();
                Optional<ClientHistory> history = clientHistoryService.createHistoryFromSlackRegForm(client, newClient, ClientHistory.Type.SLACK_UPDATE);
                history.ifPresent(client::addHistory);
                client.setName(name);
                client.setLastName(lastName);
                client.setEmail(email);
                client.setSlackInviteLink(null);
                clientRepository.saveAndFlush(client);
                slackInviteLinkRepository.deleteByClient(client);
                return slackService.inviteToWorkspace(name, lastName, email);
            }
        }
        return false;
    }

    @Override
    public Optional<String> generateSlackInviteLink(Long clientId) {
        String slackInviteLink = projectPropertiesService.getOrCreate().getSlackInviteLink();
        Optional<Client> clientOpt = getClientByID(clientId);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            String hash = clientRepository.getSlackLinkHashForClient(client);
            if (hash == null) {
                SlackInviteLink newLink = new SlackInviteLink();
                newLink.setClient(client);
                String newHash = UUID.randomUUID().toString();
                newLink.setHash(newHash);
                client.setSlackInviteLink(newLink);
                slackInviteLinkRepository.saveAndFlush(newLink);
                clientRepository.saveAndFlush(client);
                return Optional.of(slackInviteLink + newHash);
            }
            return Optional.of(slackInviteLink + hash);
        }
        return Optional.empty();
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
    public List<Client> getAllClientsSortingByLastChange() {
        List<Client> list = clientRepository.findAll();
        list.sort(getCompareLastChange());
        return list;
    }

    private Comparator<Client> getCompareLastChange() {
        return (o1, o2) -> {
            if (getLastChange(o1).isAfter(getLastChange(o2))) {
                return 1;
            } else {
                return -1;
            }
        };
    }

    private ZonedDateTime getLastChange(Client client) {
        Optional<Comment> lastComment = getLastComment(client);
        Optional<ClientHistory> lastHistory = getLastHistory(client);
        if (lastComment.isPresent()) {
            if (lastComment.get().getDateFormat().isAfter(lastHistory.get().getDate())) {
                return lastComment.get().getDateFormat();
            }
        }
        return lastHistory.get().getDate();
    }

    @Override
    public List<Client> getFilteringAndSortClients(FilteringCondition filteringCondition, String sortColumn) {
        List<Client> clients = clientRepository.filteringClientWithoutPaginator(filteringCondition);
        switch (sortColumn) {
            case "name":
                clients.sort(Comparator.comparing(client -> client.getName().toLowerCase()));
                break;
            case "lastName":
                clients.sort(Comparator.comparing(Client::getLastName));
                break;
            case "phoneNumber":
                clients.sort(Comparator.comparing(client -> client.getPhoneNumber().orElse(StringUtils.EMPTY)));
                break;
            case "email":
                clients.sort(Comparator.comparing(client -> client.getEmail().orElse(StringUtils.EMPTY)));
                break;
            case "city":
                clients.sort(Comparator.comparing(Client::getCity));
                break;
            case "country":
                clients.sort(Comparator.comparing(Client::getCountry));
                break;
            case "status":
                clients.sort(Comparator.comparing(client -> client.getStatus().getName()));
                break;
            case "dateOfRegistration":
                clients.sort(Comparator.comparing(Client::getDateOfRegistration));
                break;
            case "dateOfLastChange":
                clients.sort(getCompareLastChange());
                break;
        }
        return clients;
    }

    @Override
    public Optional<Comment> getLastComment(Client client) {
        if (!client.getComments().isEmpty()) {
            return Optional.of(client.getComments().get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ClientHistory> getLastHistory(Client client) {
        if (!client.getHistory().isEmpty()) {
            return Optional.of(client.getHistory().get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Client> getChangeActiveClients() {
        return clientRepository.getChangeActiveClients();
    }

    @Override
    public List<Client> getClientsByManyIds(List<Long> ids) {
        return clientRepository.getAllByIdIn(ids);
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
    public List<String> getSocialIdsForStudentsBySocialProfileType(String socialProfileType) {
        return clientRepository.getSocialIdsBySocialProfileTypeAndStudentExists(socialProfileType);
    }

    @Override
    public List<String> getSocialIdsForStudentsByStatusAndSocialProfileType(List<Status> statuses, String socialProfileType) {
        return clientRepository.getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(statuses, socialProfileType);
    }

    private Client clientFieldsTrimmer(Client client) {
        if (client.getName() != null && !client.getName().isEmpty()) {
            client.setName(client.getName().trim());
        }
        if (client.getMiddleName() != null && !client.getMiddleName().isEmpty()) {
            client.setMiddleName(client.getMiddleName().trim());
        }
        if (client.getLastName() != null && !client.getLastName().isEmpty()) {
            client.setLastName(client.getLastName().trim());
        }
        if (client.getSkype() != null && !client.getSkype().isEmpty()) {
            client.setSkype(client.getSkype().trim());
        }
        if (client.getCity() != null && !client.getCity().isEmpty()) {
            client.setCity(client.getCity().trim());
        }
        if (client.getCountry() != null && !client.getCountry().isEmpty()) {
            client.setCountry(client.getCountry().trim());
        }
        if (!client.getClientPhones().isEmpty()) {
            List<String> phones = new ArrayList<>();
            for (String phone : client.getClientPhones()) {
                if (phone != null && !phone.matches("\\s*")) {
                    phones.add(phone.trim());
                }
            }
            client.setClientPhones(phones);
        }
        if (!client.getClientEmails().isEmpty()) {
            List<String> emails = new ArrayList<>();
            for (String email : client.getClientEmails()) {
                if (email != null && !email.matches("\\s*")) {
                    emails.add(email.trim());
                }
            }
            client.setClientEmails(emails);
        }
        if (client.getUniversity() != null && !client.getUniversity().isEmpty()) {
            client.setUniversity(client.getUniversity().trim());
        }
        return client;
    }

    @Override
    public void addClient(Client client, User user) {
        if (user == null) {
            user = userService.get(1L);
        }

        clientFieldsTrimmer(client);
        if (client.getLastName() == null) {
            client.setLastName("");
        }

        Optional<Client> existClient = Optional.empty();

        if (client.getPhoneNumber().isPresent()) {
            client.setPhoneNumber(phoneValidator.phoneRestore(client.getPhoneNumber().get()));
        }

        if (client.getPhoneNumber().isPresent() && !client.getPhoneNumber().get().isEmpty()) {
            client.setCanCall(true);
            String validatePhone = phoneValidator.phoneRestore(client.getPhoneNumber().get());
            existClient = Optional.ofNullable(clientRepository.getClientByPhoneNumber(validatePhone));
        }

        if (!existClient.isPresent() && client.getEmail().isPresent() && !client.getEmail().get().isEmpty()) {
            existClient = Optional.ofNullable(clientRepository.getClientByEmail(client.getEmail().get()));
        }

        checkSocialIds(client);

        for (SocialProfile socialProfile : client.getSocialProfiles()) {
            if (!socialProfile.getSocialNetworkType().getName().equals("unknown")) {
                if (!existClient.isPresent()) {
                    Optional<SocialProfile> profile = socialProfileService.getSocialProfileBySocialIdAndSocialType(socialProfile.getSocialId(), socialProfile.getSocialNetworkType().getName());
                    if (profile.isPresent()) {
                        socialProfile = profile.get();
                        existClient = getClientBySocialProfile(socialProfile);
                    }
                } else {
                    break;
                }
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

            existClient.get().setClientDescriptionComment(env.getProperty("messaging.client.service.repeated"));
            existClient.get().setRepeated(true);
            sendNotificationService.sendNotificationsAllUsers(existClient.get());
            Status lastStatus = existClient.get().getStatus();
            if (client.getClientDescriptionComment().equals(env.getProperty("messaging.client.description.java-learn-link"))) {
                statusService.get("Постоплата 3").ifPresent(existClient.get()::setStatus);
            } else {
                if (client.getClientDescriptionComment().equals(env.getProperty("messaging.client.description.js-learn-link"))) {
                    statusService.get("Постоплата JS").ifPresent(existClient.get()::setStatus);
                } else {
                    statusService.getRepeatedStatusForClient().ifPresent(existClient.get()::setStatus);
                }
            }
            if (!lastStatus.equals(existClient.get().getStatus())) {
                Optional<ClientHistory> historyOfChangingStatus = clientHistoryService.createHistoryOfChangingStatus(existClient.get(), lastStatus);
                historyOfChangingStatus.ifPresent(existClient.get()::addHistory);
                ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                        ZonedDateTime.now(),
                        lastStatus,
                        existClient.get().getStatus(),
                        existClient.get(),
                        user);
                clientStatusChangingHistory.setClientCreation(true);
                clientStatusChangingHistoryService.add(clientStatusChangingHistory);
            }
            client.setId(existClient.get().getId());

            if (existClient.get().getDateOfRegistration() == null) {
                setClientDateOfRegistrationByHistoryDate(existClient.get());
            }

            clientRepository.saveAndFlush(existClient.get());
            return;
        }

        if (client.getDateOfRegistration() == null) {
            setClientDateOfRegistrationByHistoryDate(client);
        }

        clientRepository.saveAndFlush(client);
        sendNotificationService.sendNotificationsAllUsers(client);

        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                client.getDateOfRegistration(),
                null,
                client.getStatus(),
                client,
                user);
        clientStatusChangingHistory.setClientCreation(true);
        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
    }

    private void checkSocialIds(Client client) {
        for (Iterator<SocialProfile> iterator = client.getSocialProfiles().iterator(); iterator.hasNext(); ) {
            SocialProfile socialProfile = iterator.next();
            if ("vk".equals(socialProfile.getSocialNetworkType().getName()) && socialProfile.getSocialId().contains("vk")) {
                Optional<Long> id = vkService.getVKIdByUrl(socialProfile.getSocialId());
                if (id.isPresent()) {
                    socialProfile.setSocialId(String.valueOf(id.get()));
                } else {
                    client.setComment(env.getProperty("messaging.client.service.socials-not-found-comment") + socialProfile.getSocialId() + "\n" + client.getComment());
//                    client.deleteSocialProfile(socialProfile);
                    //TODO исправить ситуацию, когда не можем получить ID пользователя по ссылке vk
                }
            }
            if ("facebook".equals(socialProfile.getSocialNetworkType().getName()) && socialProfile.getSocialId().contains("facebook")) {
                String currentSocialId = socialProfile.getSocialId();
                String newSocialId = currentSocialId.substring(currentSocialId.lastIndexOf("/") + 1);
                socialProfile.setSocialId(newSocialId);
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

    /**
     * Задает клиенту дату регистрации по дате первой записи в и стории клиента.
     * Если такая запись не найдена - ставит текущую системную дату.
     *
     * @param client
     */
    @Override
    public void setClientDateOfRegistrationByHistoryDate(Client client) {
        if (client.getId() != null) {
            Optional<ClientHistory> firstHistory = clientHistoryService.getFirstByClientId(client.getId());
            if (firstHistory.isPresent()) {
                client.setDateOfRegistration(firstHistory.get().getDate());
            } else {
                client.setDateOfRegistration(ZonedDateTime.now());
            }
        } else {
            client.setDateOfRegistration(ZonedDateTime.now());
        }
    }

    @Override
    @Transactional
    public void updateClient(Client client) {
        clientFieldsTrimmer(client);
        if (client.getEmail().isPresent() && !client.getEmail().get().isEmpty()) {
            Client clientByMail = clientRepository.getClientByEmail(client.getEmail().get());
            if (clientByMail != null && !clientByMail.getId().equals(client.getId())) {
                throw new ClientExistsException(env.getProperty("messaging.client.exception.exist"));
            }
        }

        checkSocialIds(client);

        if (client.getPhoneNumber().isPresent()) {
            client.setPhoneNumber(phoneValidator.phoneRestore(client.getPhoneNumber().get()));
        }

        if (client.getPhoneNumber().isPresent() && !client.getPhoneNumber().get().isEmpty()) {
            client.setCanCall(true);
            Client clientByPhone = clientRepository.getClientByPhoneNumber(client.getPhoneNumber().get());
            if (clientByPhone != null && !client.getPhoneNumber().get().isEmpty() && !clientByPhone.getId().equals(client.getId())) {
                throw new ClientExistsException(env.getProperty("messaging.client.exception.exist"));
            }
        } else {
            client.setCanCall(false);
        }
        //checkSocialLinks(client);
        clientRepository.saveAndFlush(client);
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
    public List<Client> getOrderedClientsInStatus(Status status, SortingType order, User user) {
        List<Client> orderedClients;
        boolean isAdmin = user.getRole().contains(roleService.getRoleByName("ADMIN")) ||
                user.getRole().contains(roleService.getRoleByName("OWNER")) ||
                user.getRole().contains(roleService.getRoleByName("HR"));
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

    @Transactional
    @Override
    public void updateClientFromContractForm(Client clientOld, ContractDataForm contractForm, User user) {
        Client client = createUpdateClient(user, clientOld, contractForm);
        Optional<ClientHistory> optionalHistory = clientHistoryService.createHistory(user, clientOld, client, ClientHistory.Type.UPDATE);
        if (optionalHistory.isPresent()) {
            ClientHistory history = optionalHistory.get();
            if (history.getTitle() != null && !history.getTitle().isEmpty()) {
                client.addHistory(history);
            }
        }
        clientRepository.saveAndFlush(client);
        logger.info("{} has updated client: id {}, email {}", user.getFullName(), client.getId(), client.getEmail().orElse("not found"));
    }

    @Override
    public void setContractLink(Long clientId, String contractLink, String contractName) {
        Client client = clientRepository.getOne(clientId);
        ContractLinkData contractLinkData = new ContractLinkData();
        contractLinkData.setContractLink(contractLink);
        contractLinkData.setContractName(contractName);
        contractLinkData.setClient(client);
        client.setContractLinkData(contractLinkData);
        clientRepository.saveAndFlush(client);
    }

    private Client createUpdateClient(User user, Client old, ContractDataForm contractForm) {
        Client.Builder clientBuidlder = new Client.Builder(contractForm.getInputFirstName(), null, null);
        Client client = clientBuidlder.middleName(contractForm.getInputMiddleName())
                .lastName(contractForm.getInputLastName())
                .birthDate(contractForm.getInputBirthday())
                .build();
        String email = contractForm.getInputEmail();
        client.setId(old.getId());
        if (!email.isEmpty()) {
            Optional<Client> checkEmailClient = getClientByEmail(email);
            if (checkEmailClient.isPresent()) {
                Client clientDelEmail = checkEmailClient.get();
                Optional<ClientHistory> optionalClientHistory = clientHistoryService.createHistoryOfDeletingEmail(user, clientDelEmail, ClientHistory.Type.UPDATE);
                optionalClientHistory.ifPresent(clientDelEmail::addHistory);
                List<String> listWithCurrentEmail = clientDelEmail.getClientEmails();
                listWithCurrentEmail.remove(email);
                clientDelEmail.setClientEmails(listWithCurrentEmail);
                update(clientDelEmail);
            }
            client.setEmail(email);
        }
        String phone = contractForm.getInputPhoneNumber();
        if (!phone.isEmpty()) {
            String validatedPhone = phoneValidator.phoneRestore(phone);
            Optional<Client> checkPhoneClient = getClientByPhoneNumber(validatedPhone);
            if (checkPhoneClient.isPresent()) {
                Client clientDelPhone = checkPhoneClient.get();
                Optional<ClientHistory> optionalClientHistory = clientHistoryService.createHistoryOfDeletingPhone(user, clientDelPhone, ClientHistory.Type.UPDATE);
                optionalClientHistory.ifPresent(clientDelPhone::addHistory);
                List<String> listWithCurrentPhone = clientDelPhone.getClientPhones();
                listWithCurrentPhone.remove(validatedPhone);
                clientDelPhone.setClientPhones(listWithCurrentPhone);
                update(clientDelPhone);
            }
            client.setPhoneNumber(validatedPhone);
        }
        Passport passport = contractForm.getPassportData();
        if (passportService.encode(passport).isPresent()) {
            passport = passportService.encode(passport).get();
            passport.setClient(client);
            client.setPassport(passport);
        }
        client.setStatus(old.getStatus());
        client.setSocialProfiles(old.getSocialProfiles());
        client.setCountry(old.getCountry());
        client.setCity(old.getCity());
        client.setAge((byte) old.getAge());
        client.setSex(old.getSex());
        client.setState(old.getState());
        client.setSkype(old.getSkype());
        client.setJobs(old.getJobs());
        client.setWhatsappMessages(old.getWhatsappMessages());
        client.setHistory(old.getHistory());
        client.setComments(old.getComments());
        client.setOwnerUser(old.getOwnerUser());
        client.setStatus(old.getStatus());
        if (old.getDateOfRegistration() == null) {
            setClientDateOfRegistrationByHistoryDate(client);
        } else {
            client.setDateOfRegistration(ZonedDateTime.parse(old.getDateOfRegistration().toString()));
        }
        client.setSmsInfo(old.getSmsInfo());
        client.setCanCall(old.isCanCall());
        client.setCallRecords(old.getCallRecords());
        client.setClientDescriptionComment(old.getClientDescriptionComment());
        client.setLiveSkypeCall(old.isLiveSkypeCall());
        client.setSlackInviteLink(old.getSlackInviteLink());
        return client;
    }

    @Override
    public void transferClientsBetweenOwners(User sender, User receiver) {
        clientRepository.transferClientsBetweenOwners(sender, receiver);
        logger.info("Clients has transferred from {} to {}", sender.getFullName(), receiver.getFullName());
    }


    @Override
    public void setOtherInformationLink(Long clientId, String hash) {
        Client client = clientRepository.getOne(clientId);
        OtherInformationLinkData newOtherInformationLinkData = new OtherInformationLinkData();
        newOtherInformationLinkData.setHash(hash);
        newOtherInformationLinkData.setClient(client);
        client.setOtherInformationLinkData(newOtherInformationLinkData);
        clientRepository.saveAndFlush(client);
    }

    @Override
    public void delete(Long id) {
        notificationRepository.deleteNotificationsByClient(clientRepository.getClientById(id));
        super.delete(id);
    }
}