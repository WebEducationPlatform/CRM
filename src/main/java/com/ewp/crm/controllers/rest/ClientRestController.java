package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ClientRestController {

	private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

	private final ClientService clientService;
	private final SocialNetworkTypeService socialNetworkTypeService;
	private final UserService userService;
	private final ClientHistoryService clientHistoryService;
	private final StatusService statusService;
	private final SendNotificationService sendNotificationService;


	@Value("${project.pagination.page-size.clients}")
	private int pageSize;

	@Autowired
	public ClientRestController(ClientService clientService, SocialNetworkTypeService socialNetworkTypeService, UserService userService, ClientHistoryService clientHistoryService, StatusService statusService, VKService vkService, SendNotificationService sendNotificationService) {
		this.clientService = clientService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.userService = userService;
		this.clientHistoryService = clientHistoryService;
		this.statusService = statusService;
		this.sendNotificationService = sendNotificationService;
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/client", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAll());
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/client/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.get(id));
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/client/assign", method = RequestMethod.POST)
	public ResponseEntity<User> assign(@RequestParam(name = "clientId") Long clientId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(clientId);
		if (client.getOwnerUser() != null) {
			logger.info("User {} tried to assign a client with id {}, but client have owner", user.getEmail(), clientId);
			return ResponseEntity.badRequest().body(null);
		}
		client.setOwnerUser(user);
		client.addHistory(clientHistoryService.createHistory(user, client, ClientHistory.Type.ASSIGN));
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {}", user.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/client/assign/user", method = RequestMethod.POST)
	public ResponseEntity assignUser(@RequestParam(name = "clientId") Long clientId,
	                                 @RequestParam(name = "userForAssign") Long userId) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User assignUser = userService.get(userId);
		Client client = clientService.get(clientId);
		if (client.getOwnerUser() != null && client.getOwnerUser().equals(assignUser)) {
			logger.info("User {} tried to assign a client with id {}, but client have same owner {}", principal.getEmail(), clientId, assignUser.getEmail());
			return ResponseEntity.badRequest().build();
		}
		if (principal.equals(assignUser)) {
			client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.ASSIGN));
		} else {
			client.addHistory(clientHistoryService.createHistory(principal, assignUser, client, ClientHistory.Type.ASSIGN));
		}
		client.setOwnerUser(assignUser);
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {} to user {}", principal.getEmail(), clientId, assignUser.getEmail());
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/client/unassign", method = RequestMethod.POST)
	public ResponseEntity unassign(@RequestParam(name = "clientId") Long clientId) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(clientId);
		if (client.getOwnerUser() == null) {
			logger.info("User {} tried to unassign a client with id {}, but client already doesn't have owner", principal.getEmail(), clientId);
			return ResponseEntity.badRequest().build();
		}
		if (client.getOwnerUser().equals(principal)) {
			client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.UNASSIGN));
		} else {
			client.addHistory(clientHistoryService.createHistory(principal, client.getOwnerUser(), client, ClientHistory.Type.UNASSIGN));
		}
		client.setOwnerUser(null);
		clientService.updateClient(client);
		logger.info("User {} has unassigned client with id {}", principal.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	@RequestMapping(value = "/admin/rest/client/update", method = RequestMethod.POST)
	public ResponseEntity updateClient(@RequestBody Client currentClient) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (SocialNetwork socialNetwork : currentClient.getSocialNetworks()) {
			socialNetwork.getSocialNetworkType().setId(socialNetworkTypeService.getByTypeName(
					socialNetwork.getSocialNetworkType().getName()).getId());
		}
		Client clientFromDB = clientService.get(currentClient.getId());
		currentClient.setHistory(clientFromDB.getHistory());
		currentClient.setComments(clientFromDB.getComments());
		currentClient.setOwnerUser(clientFromDB.getOwnerUser());
		currentClient.setStatus(clientFromDB.getStatus());
		currentClient.setDateOfRegistration(clientFromDB.getDateOfRegistration());
		currentClient.setSmsInfo(clientFromDB.getSmsInfo());
		currentClient.setNotifications(clientFromDB.getNotifications());
		currentClient.setCanCall(clientFromDB.isCanCall());
		currentClient.setCallRecords(clientFromDB.getCallRecords());
		currentClient.setClientDescriptionComment(clientFromDB.getClientDescriptionComment());
		if (currentClient.equals(clientFromDB)) {
			return ResponseEntity.noContent().build();
		}
		currentClient.addHistory(clientHistoryService.createHistory(currentAdmin, clientFromDB, currentClient, ClientHistory.Type.UPDATE));
		clientService.updateClient(currentClient);
		logger.info("{} has updated client: id {}, email {}", currentAdmin.getFullName(), currentClient.getId(), currentClient.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
	@RequestMapping(value = "/admin/rest/client/add", method = RequestMethod.POST)
	public ResponseEntity addClient(@RequestBody Client client) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (SocialNetwork socialNetwork : client.getSocialNetworks()) {
			socialNetwork.getSocialNetworkType().setId(socialNetworkTypeService.getByTypeName(
					socialNetwork.getSocialNetworkType().getName()).getId());
		}
		Status status = statusService.get(client.getStatus().getName());
		client.setStatus(status);
		client.addHistory(clientHistoryService.createHistory(principal, client, client, ClientHistory.Type.ADD));
		clientService.addClient(client);
		logger.info("{} has added client: id {}, email {}", principal.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/rest/client/filtration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAllWithConditions(@RequestBody FilteringCondition filteringCondition) {
		List<Client> clients = clientService.filteringClient(filteringCondition);
		return ResponseEntity.ok(clients);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/client/createFile", method = RequestMethod.POST)
	public ResponseEntity createFile(@RequestParam(name = "selected") String selected) {

		String path = "DownloadData";
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				logger.error("Could not create folder for text files");
			}
		}
		String fileName = "data.txt";
		File file = new File(dir, fileName);
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					logger.error("File for clients not created!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


			if (Optional.ofNullable(socialNetworkTypeService.getByTypeName(selected)).isPresent()) {
				List<SocialNetwork> socialNetworks = socialNetworkTypeService.getByTypeName(selected).getSocialNetworkList();
				for (SocialNetwork socialNetwork : socialNetworks) {
					bufferedWriter.write(socialNetwork.getLink() + "\r\n");
				}
			}
			if (selected.equals("email")) {
				List<String> emails = clientService.getClientsEmails();
				for (String email : emails) {
					if (email == null) {
						email = "";
					}
					bufferedWriter.write(email + "\r\n");
				}
			}
			if (selected.equals("phoneNumber")) {
				List<String> phoneNumbers = clientService.getClientsPhoneNumbers();
				for (String phoneNumber : phoneNumbers) {
					if (phoneNumber == null) {
						phoneNumber = "";
					}
					bufferedWriter.write(phoneNumber + "\r\n");
				}
			}

			bufferedWriter.close();
		} catch (IOException e) {
			logger.error("File not created! ", e);
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/client/createFileFilter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity createFileWithFilter(@RequestBody FilteringCondition filteringCondition) {

		String path = "DownloadData";
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				logger.error("Could not create folder for text files");
			}
		}
		String fileName = "data.txt";
		File file = new File(dir, fileName);
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					logger.error("Text file for filtered clients not created!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			if (Optional.ofNullable(socialNetworkTypeService.getByTypeName(filteringCondition.getSelected())).isPresent()) {
				List<String> socialNetworkLinks = clientService.getFilteredClientsSNLinks(filteringCondition);
				for (String socialNetworkLink : socialNetworkLinks) {
					bufferedWriter.write(socialNetworkLink + "\r\n");
				}
			}
			if (filteringCondition.getSelected().equals("email")) {
				List<String> emails = clientService.getFilteredClientsEmail(filteringCondition);
				for (String email : emails) {
					if (email == null) {
						email = "";
					}
					bufferedWriter.write(email + "\r\n");
				}
			}
			if (filteringCondition.getSelected().equals("phoneNumber")) {
				List<String> phoneNumbers = clientService.getFilteredClientsPhoneNumber(filteringCondition);
				for (String phoneNumber : phoneNumbers) {
					if (phoneNumber == null) {
						phoneNumber = "";
					}
					bufferedWriter.write(phoneNumber + "\r\n");
				}
			}

			bufferedWriter.close();
		} catch (IOException e) {
			logger.error("File not created! ", e);
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/client/getClientsData", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getClientsData() {

		String path = "DownloadData\\";
		File file = new File(path + "data.txt");

		InputStreamResource resource = null;
		try {
			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("File not found! ", e);
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment;filename=" + file.getName())
				.contentType(MediaType.TEXT_PLAIN).contentLength(file.length())
				.body(resource);
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/client/postpone", method = RequestMethod.POST)
	public ResponseEntity postponeClient(@RequestParam Long clientId, @RequestParam String date) {
		try {
			Client client = clientService.get(clientId);
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
			LocalDateTime postponeDate = LocalDateTime.parse(date, dateTimeFormatter);
			if (postponeDate.isBefore(LocalDateTime.now()) || postponeDate.isEqual(LocalDateTime.now())) {
				logger.info("Wrong postpone date: {}", date);
				return ResponseEntity.badRequest().body("Дата должна быть позже текущей даты");
			}
			client.setPostponeDate(postponeDate.toDate());
			User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			client.setOwnerUser(principal);
			client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.POSTPONE));
			clientService.updateClient(client);
			logger.info("{} has postponed client id:{} until {}", principal.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}


	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "rest/client/addDescription", method = RequestMethod.POST)
	public ResponseEntity<String> addDescription(@RequestParam(name = "clientId") Long clientId,
	                                             @RequestParam(name = "clientDescription") String clientDescription) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(clientId);
		if (client == null) {
			logger.error("Can`t add description, client with id {} not found or description is the same", clientId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("client not found or description is same");
		}
		if (client.getClientDescriptionComment() != null && client.getClientDescriptionComment().equals(clientDescription)) {
			logger.error("Client has same description");
			return ResponseEntity.badRequest().body("Client has same description");
		}
		client.setClientDescriptionComment(clientDescription);
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.DESCRIPTION));
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body(clientDescription);
	}

	@GetMapping("rest/client/pagination/get")
	public ResponseEntity getClients(@RequestParam int page) {
		List<Client> clients = clientService.findAllByPage(new PageRequest(page, pageSize));
		if (clients == null || clients.isEmpty()) {
			logger.error("No more clients");
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(clients);
	}
}