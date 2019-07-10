package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.ConditionToDownload;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.Message;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.ClientCardDtoBuilder;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.MessageService;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.ReportService;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/client")
public class ClientRestController {

    private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    private final ClientService clientService;
    private final UserService userService;
    private final ClientHistoryService clientHistoryService;
    private final MessageService messageService;
    private final ProjectPropertiesService propertiesService;
    private final SocialProfileService socialProfileService;
    private final StatusService statusService;
    private final StudentService studentService;
    private final StudentStatusService studentStatusService;
    private final ReportService reportService;
    private String fileName;
    private final ClientRepository clientRepository;
    private Environment env;

    @Value("${project.pagination.page-size.clients}")
    private int pageSize;

    @Autowired
    public ClientRestController(ClientService clientService,
                                UserService userService,
                                SocialProfileService socialProfileService,
                                ClientHistoryService clientHistoryService,
                                MessageService messageService,
                                ProjectPropertiesService propertiesService,
                                StatusService statusService,
                                StudentService studentService,
                                StudentStatusService studentStatusService,
                                ReportService reportService,
                                ClientRepository clientRepository, Environment env) {
        this.clientService = clientService;
        this.userService = userService;
        this.clientHistoryService = clientHistoryService;
        this.messageService = messageService;
        this.propertiesService = propertiesService;
        this.socialProfileService = socialProfileService;
        this.statusService = statusService;
        this.studentService = studentService;
        this.studentStatusService = studentStatusService;
        this.reportService = reportService;
        this.fileName = new String();
        this.clientRepository = clientRepository;
        this.env = env;
    }

    @GetMapping(value = "/slack-invite-link/{clientId}")
    public ResponseEntity<String> generateSlackInviteLink(@PathVariable("clientId") Long clientId) {
        Optional<String> inviteLink = clientService.generateSlackInviteLink(clientId);
        return inviteLink.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().body(""));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }

    //запрос для вывода клиентов постранично - порядок из базы
    @GetMapping(value = "/pagination/get")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity getClients(@RequestParam int page) {
        List<Client> clients = clientService.getAllClientsByPage(PageRequest.of(page, pageSize));
        if (clients == null || clients.isEmpty()) {
            logger.info("No more clients");
        }
        return ResponseEntity.ok(clients);
    }

    //запрос для вывода клиентов постранично - новые выше (16.10.18 установлен по дефолту для all-clients-table)
    @GetMapping(value = "/pagination/new/first")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity getClientsNewFirst(@RequestParam int page) {
        List<Client> clients = clientService.getAllClientsByPage(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "dateOfRegistration")));
        if (clients == null || clients.isEmpty()) {
            logger.info("No more clients");
        }
        return ResponseEntity.ok(clients);
    }

    @GetMapping(value = "/sort")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity getClientsSort(@RequestParam int page, @RequestParam String columnName, @RequestParam boolean sortType) {
        if (columnName.equals("dateOfLastChange")) {
            List<Client> clients = clientService.getAllClientsSortingByLastChange();
            if (sortType) {
                Collections.reverse(clients);
            }
            List<Client> listClientsOnPage = getClientsOnPage(clients, page);
            return ResponseEntity.ok(listClientsOnPage);
        } else {
            List<Client> clients = clientService.getAllClientsByPage(PageRequest.of(page, pageSize, Sort.by(sortType ? Sort.Direction.ASC : Sort.Direction.DESC, columnName)));
            if (clients.isEmpty()) {
                logger.info("No more clients");
            }
            return ResponseEntity.ok(clients);
        }
    }

    private List<Client> getClientsOnPage(List<Client> allClients, int page) {
        int sizeOnePage = allClients.size() / (pageSize / 2);
        if (sizeOnePage < 15) {
            sizeOnePage = allClients.size();
        }
        int clientsOnPage = page * sizeOnePage;
        List<Client> listClientsOnPage = new ArrayList<>();

        int i = 0;
        if (page > 1) {
            i = (page - 1) * sizeOnePage;
        }
        for (; i < clientsOnPage; i++) {
            if (allClients.get(i) != null) {
                listClientsOnPage.add(allClients.get(i));
            }
        }
        return listClientsOnPage;
    }

    @PostMapping(value = "/filtration/sort", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity getAllWithConditionsAndSort(@RequestParam String columnName, @RequestParam boolean sortType, @RequestBody FilteringCondition filteringCondition) {
        List<Client> clients = clientService.getFilteringAndSortClients(filteringCondition, columnName);
        if (sortType) {
            Collections.reverse(clients);
        }
        List<Client> listClientsOnPage = getClientsOnPage(clients, filteringCondition.getPageNumber());
        if (listClientsOnPage.isEmpty()) {
            logger.info("No more clients");
        }
        return ResponseEntity.ok(listClientsOnPage);
    }

    @GetMapping(value = "/getClientsData")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<InputStreamResource> getClientsData() throws UnsupportedEncodingException {
        String path = "DownloadData" + File.separator;
        File file = new File(path + fileName);
        String encodedFileName = URLEncoder.encode(file.getName(), "UTF-8");
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("File not found! ", e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + encodedFileName)
                .contentType(MediaType.TEXT_PLAIN).contentLength(file.length())
                .body(resource);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.get(id));
    }

    @GetMapping(value = "/socialID", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<Map<String, String>> getClientBySocialProfile(@RequestParam(name = "userID") String socialId,
                                                                        @RequestParam(name = "socialNetworkType") String socialNetworkType,
                                                                        @RequestParam(name = "unread") String unreadCount) {
        Map<String, String> clientInfoMap = new HashMap<>();
        Optional<SocialProfile> socialProfile = socialProfileService.getSocialProfileBySocialIdAndSocialType(socialId, socialNetworkType);
        if (socialProfile.isPresent()) {
            Optional<Client> client = clientService.getClientBySocialProfile(socialProfile.get());
            if (!client.isPresent()) {
                clientInfoMap.put("clientID", "0");
            } else {
                clientInfoMap.put("clientID", Long.toString(client.get().getId()));
            }
        } else {
            clientInfoMap.put("clientID", "0");
        }
        clientInfoMap.put("unreadCount", unreadCount.isEmpty() ? "" : unreadCount);
        clientInfoMap.put("userID", socialId);
        return ResponseEntity.ok(clientInfoMap);
    }

    @PostMapping(value = "/assign")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<User> assign(@RequestParam(name = "clientId") Long clientId,
                                       @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        if (client.getOwnerUser() != null) {
            logger.info("User {} tried to assign a client with id {}, but client have owner", userFromSession.getEmail(), clientId);
            return ResponseEntity.badRequest().body(null);
        }
        client.setOwnerUser(userFromSession);
        clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ASSIGN).ifPresent(client::addHistory);
        clientService.updateClient(client);
        logger.info("User {} has assigned client with id {}", userFromSession.getEmail(), clientId);
        return ResponseEntity.ok(client.getOwnerUser());
    }

    @PostMapping(value = "/assign/user")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity assignUser(@RequestParam(name = "clientId") Long clientId,
                                     @RequestParam(name = "userForAssign") Long userId,
                                     @AuthenticationPrincipal User userFromSession) {
        User assignUser = userService.get(userId);
        Client client = clientService.get(clientId);
        if (client.getOwnerUser() != null && client.getOwnerUser().equals(assignUser)) {
            logger.info("User {} tried to assign a client with id {}, but client have same owner {}", userFromSession.getEmail(), clientId, assignUser.getEmail());
            return ResponseEntity.badRequest().build();
        }
        if (userFromSession.equals(assignUser)) {
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ASSIGN).ifPresent(client::addHistory);
        } else {
            clientHistoryService.createHistory(userFromSession, assignUser, client, ClientHistory.Type.ASSIGN).ifPresent(client::addHistory);
        }
        client.setOwnerUser(assignUser);
        clientService.updateClient(client);
        logger.info("User {} has assigned client with id {} to user {}", userFromSession.getEmail(), clientId, assignUser.getEmail());
        return ResponseEntity.ok(client.getOwnerUser());
    }

    @PostMapping(value = "/assign/mentor")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity assignMentor(@RequestParam(name = "clientId") Long clientId,
                                       @RequestParam(name = "userForAssign") Long userId,
                                       @AuthenticationPrincipal User userFromSession) {
        User assignUser = userService.get(userId);
        Client client = clientService.get(clientId);
        if (client.getOwnerMentor() != null && client.getOwnerMentor().equals(assignUser)) {
            logger.info("User {} tried to assign a client with id {}, but client have same owner {}", userFromSession.getEmail(), clientId, assignUser.getEmail());
            return ResponseEntity.badRequest().build();
        }
        if (userFromSession.equals(assignUser)) {
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ASSIGN_MENTOR).ifPresent(client::addHistory);
        } else {
            clientHistoryService.createHistory(userFromSession, assignUser, client, ClientHistory.Type.ASSIGN_MENTOR).ifPresent(client::addHistory);
        }
        client.setOwnerMentor(assignUser);
        clientService.updateClient(client);
        logger.info("User {} has assigned client with id {} to user {}", userFromSession.getEmail(), clientId, assignUser.getEmail());
        return ResponseEntity.ok(client.getOwnerMentor());
    }

    @PostMapping(value = "/unassign")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity unassign(@RequestParam(name = "clientId") Long clientId,
                                   @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        if (client.getOwnerUser() == null) {
            logger.info("User {} tried to unassign a client with id {}, but client already doesn't have owner", userFromSession.getEmail(), clientId);
            return ResponseEntity.badRequest().build();
        }
        if (client.getOwnerUser().equals(userFromSession)) {
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.UNASSIGN).ifPresent(client::addHistory);
        } else {
            clientHistoryService.createHistory(userFromSession, client.getOwnerUser(), client, ClientHistory.Type.UNASSIGN).ifPresent(client::addHistory);
        }
        client.setOwnerUser(null);
        clientService.updateClient(client);
        logger.info("User {} has unassigned client with id {}", userFromSession.getEmail(), clientId);
        return ResponseEntity.ok(client.getOwnerUser());
    }

    @PostMapping(value = "/unassignMentor")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity unassignMentor(@RequestParam(name = "clientId") Long clientId,
                                         @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        if (client.getOwnerMentor() == null) {
            logger.info("User {} tried to unassign a client with id {}, but client already doesn't have owner", userFromSession.getEmail(), clientId);
            return ResponseEntity.badRequest().build();
        }
        if (client.getOwnerMentor().equals(userFromSession)) {
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.UNASSIGN_MENTOR).ifPresent(client::addHistory);
        } else {
            clientHistoryService.createHistory(userFromSession, client.getOwnerMentor(), client, ClientHistory.Type.UNASSIGN_MENTOR).ifPresent(client::addHistory);
        }
        client.setOwnerMentor(null);
        clientService.updateClient(client);
        logger.info("User {} has unassigned client with id {}", userFromSession.getEmail(), clientId);
        return ResponseEntity.ok(client.getOwnerMentor());
    }

    @PostMapping(value = "/filtration", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<Client>> getAllWithConditions(@RequestBody FilteringCondition filteringCondition) {
        List<Client> clients = clientService.filteringClient(filteringCondition);
        return ResponseEntity.ok(clients);
    }

    @PostMapping(value = "/filtrationWithoutPagination", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<Client>> getAllWithConditionsWithoutPagination(@RequestBody FilteringCondition filteringCondition) {
        List<Client> clients = clientRepository.filteringClientWithoutPaginator(filteringCondition);
        return ResponseEntity.ok(clients);
    }

    @PostMapping(value = "/createFile")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity createFile(@RequestBody ConditionToDownload conditionToDownload) {
        fileName = reportService.getFileName(conditionToDownload.getSelected(),
                conditionToDownload.getDelimeter(), conditionToDownload.getFiletype(), null).get();
        if (conditionToDownload.getFiletype().equals("txt")) {
            reportService.writeToFileWithConditionToDownload(conditionToDownload, fileName);
        } else if (conditionToDownload.getFiletype().equals("xlsx")) {
            reportService.writeToExcelFileWithConditionToDownload(conditionToDownload, fileName);
        } else if (conditionToDownload.getFiletype().equals("csv")) {
            reportService.writeToCSVFileWithConditionToDownload(conditionToDownload, fileName);
        } else {
            return ResponseEntity.badRequest().body("Can't parse condition to download.");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/createFileFilter")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity createFileWithFilter(@RequestBody FilteringCondition filteringCondition) {
        fileName = reportService.getFileName(filteringCondition.getSelectedCheckbox(),
                filteringCondition.getDelimeter(), filteringCondition.getFiletype(), filteringCondition.getStatus()).get();
        if (filteringCondition.getFiletype().equals("txt")) {
            reportService.writeToFileWithFilteringConditions(filteringCondition, fileName);
        } else if (filteringCondition.getFiletype().equals("xlsx")) {
            reportService.writeToExcelFileWithFilteringConditions(filteringCondition, fileName);
        } else if (filteringCondition.getFiletype().equals("csv")) {
            reportService.writeToCSVFileWithFilteringConditions(filteringCondition, fileName);
        } else {
            return ResponseEntity.badRequest().body("Can't parse filtering condition.");
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/postpone")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity postponeClient(@RequestParam Long clientId,
                                         @RequestParam String date,
                                         @RequestParam Boolean isPostponeFlag,
                                         @RequestParam String postponeComment,
                                         @AuthenticationPrincipal User userFromSession) {
        try {
            Client client = clientService.get(clientId);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            ZonedDateTime postponeDate = LocalDateTime.parse(date, dateTimeFormatter).atZone(ZoneId.systemDefault());
            if (postponeDate.isBefore(ZonedDateTime.now()) || postponeDate.isEqual(ZonedDateTime.now())) {
                logger.info("Wrong postpone date: {}", date);
                return ResponseEntity.badRequest().body(env.getProperty("messaging.client.rest.wrong-postpone-date"));
            }
            if (isPostponeFlag) {
                client.setHideCard(true);
            }
            client.setPostponeComment(postponeComment);
            client.setPostponeDate(postponeDate);
            client.setOwnerUser(userFromSession);
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.POSTPONE).ifPresent(client::addHistory);
            clientService.updateClient(client);
            logger.info("{} has postponed client id:{} until {}", userFromSession.getFullName(), client.getId(), date);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(env.getProperty("messaging.client.rest.postpone-error"));
        }
    }

    @PostMapping(value = "/remove/postpone")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity removePostpone(@RequestParam Long clientId,
                                         @AuthenticationPrincipal User userFromSession) {
        try {
            Client client = clientService.get(clientId);
            client.setHideCard(false);
            client.setPostponeDate(null);
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.REMOVE_POSTPONE).ifPresent(client::addHistory);
            clientService.updateClient(client);
            logger.info("{} remove from postpone client id:{}", userFromSession.getFullName(), client.getId());
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(env.getProperty("messaging.client.rest.postpone-error"));
        }
    }

    @PostMapping(value = "/addDescription")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<String> addDescription(@RequestParam(name = "clientId") Long clientId,
                                                 @RequestParam(name = "clientDescription") String clientDescription,
                                                 @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        if (client == null) {
            logger.error("Can`t add description, client with id {} not found or description is the same", clientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(env.getProperty("messaging.client.rest.description-error"));
        }
        if (client.getClientDescriptionComment() != null && client.getClientDescriptionComment().equals(clientDescription)) {
            logger.error("Client has same description");
            return ResponseEntity.badRequest().body(env.getProperty("messaging.client.rest.description-same-error"));
        }
        client.setClientDescriptionComment(clientDescription);
        clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.DESCRIPTION).ifPresent(client::addHistory);
        clientService.updateClient(client);
        return ResponseEntity.status(HttpStatus.OK).body(clientDescription);
    }

    @PostMapping(value = "/setSkypeLogin")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<String> setClientSkypeLogin(@RequestParam(name = "clientId") Long clientId,
                                                      @RequestParam(name = "skypeLogin") String skypeLogin,
                                                      @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        Optional<Client> checkDuplicateLogin = clientService.getClientBySkype(skypeLogin);
        if (client == null) {
            logger.error("Client with id {} not found", clientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(env.getProperty("messaging.client.rest.description-error"));
        }
        if (checkDuplicateLogin.isPresent() && checkDuplicateLogin.get().getSkype().equals(skypeLogin)) {
            logger.error("client with this skype login already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(env.getProperty("messaging.client.rest.skype-login-same-error"));
        }
        client.setSkype(skypeLogin);
        clientHistoryService.createInfoHistory(userFromSession, client, ClientHistory.Type.ADD_LOGIN, skypeLogin).ifPresent(client::addHistory);
        clientService.updateClient(client);
        return ResponseEntity.status(HttpStatus.OK).body(skypeLogin);
    }

    @GetMapping(value = "/message/info/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<Message> getClientMessageInfoByID(@PathVariable("id") Long id) {
        return new ResponseEntity<>(messageService.get(id), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<List<Client>> getClientsBySearchPhrase(@RequestParam(name = "search") String search) {
        return new ResponseEntity<>(clientService.getClientsBySearchPhrase(search), HttpStatus.OK);
    }

    @PostMapping(value = "/postpone/getComment")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getPostponeComment(@RequestParam Long clientId) {
        String postponeComment = clientService.get(clientId).getPostponeComment();
        return ResponseEntity.status(HttpStatus.OK).body(postponeComment);
    }

    @PostMapping(value = "/setRepeated")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR')")
    public ResponseEntity<String> setRepeated(@RequestParam(name = "clientId") Long clientId,
                                              @RequestParam(name = "isRepeated") Boolean isRepeated,
                                              @AuthenticationPrincipal User userFromSession) {
        Client client = clientService.get(clientId);
        if (client == null) {
            logger.error("Can`t add description, client with id {} not found or description is the same", clientId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(env.getProperty("messaging.client.rest.description-error"));
        }
        if (client.isRepeated()) {
            clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.DESCRIPTION).ifPresent(client::addHistory);
        }
        client.setRepeated(isRepeated);

        clientService.updateClient(client);
        return ResponseEntity.status(HttpStatus.OK).body(env.getProperty("messaging.client.rest.repeated-client-updated"));
    }

    @PostMapping(value = "/order")
    public ResponseEntity setNewClientsOrder(@RequestParam SortingType newOrder,
                                             @RequestParam Long statusId,
                                             @AuthenticationPrincipal User userFromSession) {
        statusService.setNewOrderForChosenStatusForCurrentUser(newOrder, statusId, userFromSession);
        return ResponseEntity.ok("Ok");
    }

    @GetMapping(value = "/order")
    public ResponseEntity<SortingType> getClientsOrder(@RequestParam(name = "statusId") final Long statusId,
                                                       @AuthenticationPrincipal final User userFromSession) {
        final Optional<SortingType> optional = statusService.findOrderForChosenStatusForCurrentUser(statusId, userFromSession);
        SortingType sortingType;
        if (optional.isPresent()) {
            sortingType = optional.get();
        } else {
            sortingType = SortingType.NEW_FIRST;
            statusService.setNewOrderForChosenStatusForCurrentUser(sortingType, statusId, userFromSession);
        }

        return new ResponseEntity<>(sortingType, HttpStatus.OK);
    }

    @PostMapping(value = "/massInputSend")
    public void massClientInputSave(@RequestParam(name = "emailList") String emailList,
                                    @RequestParam(name = "fioList") String fioList,
                                    @RequestParam(name = "trialDateList") String trialDateList,
                                    @RequestParam(name = "nextPaymentList") String nextPaymentList,
                                    @RequestParam(name = "priceList") String priceList,
                                    @RequestParam(name = "paymentSumList") String paymentSumList,
                                    @RequestParam(name = "studentStatus") String studentStatusId) {

        List<String> resultEmailList = Arrays.asList(emailList.split("\n"));
        List<String> resultFioList = Arrays.asList(fioList.split("\n"));
        List<String> resultTrialDateList = Arrays.asList(trialDateList.split("\n"));
        List<String> resultNextPaymentList = Arrays.asList(nextPaymentList.split("\n"));
        List<String> resultPriceList = Arrays.asList(priceList.split("\n"));
        List<String> resultPaymentSumList = Arrays.asList(paymentSumList.split("\n"));
        StudentStatus studentStatus = studentStatusService.get(Long.valueOf(studentStatusId));

        for (int i = 0; i < resultEmailList.size(); i++) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate endTrialDate = LocalDate.parse(resultTrialDateList.get(i), dateTimeFormatter);
            LocalDate nextPaymentDate = LocalDate.parse(resultNextPaymentList.get(i), dateTimeFormatter);
            BigDecimal price = new BigDecimal(resultPriceList.get(i));
            BigDecimal paymentAmount = new BigDecimal(resultPaymentSumList.get(i));
            String[] nameAndLastNameArr = resultFioList.get(i).split(" ");
            Optional<Client> client = clientService.getClientByEmail(resultEmailList.get(i).trim());
            if (!client.isPresent()) {
                client = clientService.findByNameAndLastNameIgnoreCase(nameAndLastNameArr[0].trim(), nameAndLastNameArr[1].trim());
            }
            if (client.isPresent()) {
                Optional<Student> student = studentService.getStudentByClientId(client.get().getId());
                if (student.isPresent()) {
                    studentService.update(setStudentParams(student.get(), endTrialDate, nextPaymentDate, price, paymentAmount, studentStatus));
                } else {
                    Student newStudent = new Student();
                    newStudent.setClient(client.get());
                    studentService.save(setStudentParams(newStudent, endTrialDate, nextPaymentDate, price, paymentAmount, studentStatus));
                }
            } else {
                Client.Builder newClientBuilder = new Client.Builder(nameAndLastNameArr[0].trim(), null, resultEmailList.get(i).trim());
                Client newClient = newClientBuilder.lastName(nameAndLastNameArr[1].trim()).build();
                statusService.get(1L).ifPresent(newClient::setStatus);
                clientService.addClient(newClient);
                Student createStudent = new Student();
                createStudent.setClient(newClient);
                studentService.save(setStudentParams(createStudent, endTrialDate, nextPaymentDate, price, paymentAmount, studentStatus));
            }
        }
    }

    public Student setStudentParams(Student student, LocalDate endTrialDate, LocalDate nextPaymentDate, BigDecimal price, BigDecimal paymentAmount, StudentStatus studentStatus) {
        student.setTrialEndDate(endTrialDate.atStartOfDay());
        student.setNextPaymentDate(nextPaymentDate.atStartOfDay());
        student.setPrice(price);
        student.setPaymentAmount(paymentAmount);
        student.setPayLater(new BigDecimal(0));
        student.setStatus(studentStatus);
        return student;
    }

    @GetMapping("/card/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR')")
    public ResponseEntity<String> getClientCardDto(@PathVariable Long id) {
        return ResponseEntity.ok(ClientCardDtoBuilder.buildClientCardDto(clientService.get(id), statusService.getAll()));
    }

    @GetMapping
    public ResponseEntity<Student> getStudentByEmail(@RequestParam("email") String email) {
        ResponseEntity result;
        try {
            Client client = clientService.getClientByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Client with email not found" + email));
            result = ResponseEntity.ok(client);
        } catch (RuntimeException rte) {
            logger.info("Student with email {} not found", email);
            result = new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return result;
    }
}
