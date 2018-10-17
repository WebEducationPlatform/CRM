package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/rest/client")
public class ClientRestController {

	private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

	private final ClientService clientService;
	private final SocialProfileTypeService socialProfileTypeService;
	private final UserService userService;
	private final ClientHistoryService clientHistoryService;
	private final MessageService messageService;

	@Value("${project.pagination.page-size.clients}")
	private int pageSize;

	@Autowired
	public ClientRestController(ClientService clientService,
								SocialProfileTypeService socialProfileTypeService,
								UserService userService,
								ClientHistoryService clientHistoryService, MessageService messageService) {
		this.clientService = clientService;
		this.socialProfileTypeService = socialProfileTypeService;
		this.userService = userService;
		this.clientHistoryService = clientHistoryService;
		this.messageService = messageService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAll());
	}

	@GetMapping(value = "/pagination/get")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity getClients(@RequestParam int page) {
		List<Client> clients = clientService.getAllClientsByPage(new PageRequest(page, pageSize));
		if (clients == null || clients.isEmpty()) {
			logger.error("No more clients");
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(clients);
	}

	@GetMapping(value = "/getClientsData")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
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

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.get(id));
	}

	@PostMapping(value = "/assign")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<User> assign(@RequestParam(name = "clientId") Long clientId,
									   @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(clientId);
		if (client.getOwnerUser() != null) {
			logger.info("User {} tried to assign a client with id {}, but client have owner", userFromSession.getEmail(), clientId);
			return ResponseEntity.badRequest().body(null);
		}
		client.setOwnerUser(userFromSession);
		client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ASSIGN));
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {}", userFromSession.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@PostMapping(value = "/assign/user")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
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
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ASSIGN));
		} else {
			client.addHistory(clientHistoryService.createHistory(userFromSession, assignUser, client, ClientHistory.Type.ASSIGN));
		}
		client.setOwnerUser(assignUser);
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {} to user {}", userFromSession.getEmail(), clientId, assignUser.getEmail());
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@PostMapping(value = "/unassign")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity unassign(@RequestParam(name = "clientId") Long clientId,
								   @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(clientId);
		if (client.getOwnerUser() == null) {
			logger.info("User {} tried to unassign a client with id {}, but client already doesn't have owner", userFromSession.getEmail(), clientId);
			return ResponseEntity.badRequest().build();
		}
		if (client.getOwnerUser().equals(userFromSession)) {
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.UNASSIGN));
		} else {
			client.addHistory(clientHistoryService.createHistory(userFromSession, client.getOwnerUser(), client, ClientHistory.Type.UNASSIGN));
		}
		client.setOwnerUser(null);
		clientService.updateClient(client);
		logger.info("User {} has unassigned client with id {}", userFromSession.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@PostMapping(value = "/filtration", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<List<Client>> getAllWithConditions(@RequestBody FilteringCondition filteringCondition) {
		List<Client> clients = clientService.filteringClient(filteringCondition);
		return ResponseEntity.ok(clients);
	}

	@PostMapping(value = "/createFile")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
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


			if (Optional.ofNullable(socialProfileTypeService.getByTypeName(selected)).isPresent()) {
				List<SocialProfile> socialProfiles = socialProfileTypeService.getByTypeName(selected).getSocialProfileList();
				for (SocialProfile socialProfile : socialProfiles) {
					bufferedWriter.write(socialProfile.getLink() + "\r\n");
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

	@PostMapping(value = "/createFileFilter", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
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

			if (Optional.ofNullable(socialProfileTypeService.getByTypeName(filteringCondition.getSelected())).isPresent()) {
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

	@PostMapping(value = "/postpone")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity postponeClient(@RequestParam Long clientId,
										 @RequestParam String date,
										 @AuthenticationPrincipal User userFromSession) {
		try {
			Client client = clientService.get(clientId);
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
			ZonedDateTime postponeDate = LocalDateTime.parse(date, dateTimeFormatter).atZone(ZoneId.systemDefault());
			if (postponeDate.isBefore(ZonedDateTime.now()) || postponeDate.isEqual(ZonedDateTime.now())) {
				logger.info("Wrong postpone date: {}", date);
				return ResponseEntity.badRequest().body("Дата должна быть позже текущей даты");
			}
			client.setPostponeDate(postponeDate);
			client.setOwnerUser(userFromSession);
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.POSTPONE));
			clientService.updateClient(client);
			logger.info("{} has postponed client id:{} until {}", userFromSession.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}

	@PostMapping(value = "/remove/postpone")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity removePostpone(@RequestParam Long clientId,
										 @AuthenticationPrincipal User userFromSession) {
		try {
			Client client = clientService.get(clientId);
			client.setPostponeDate(null);
			client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.REMOVE_POSTPONE));
			clientService.updateClient(client);
			logger.info("{} remove from postpone client id:{}", userFromSession.getFullName(), client.getId());
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
	}

	@PostMapping(value = "/addDescription")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<String> addDescription(@RequestParam(name = "clientId") Long clientId,
												 @RequestParam(name = "clientDescription") String clientDescription,
												 @AuthenticationPrincipal User userFromSession) {
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
		client.addHistory(clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.DESCRIPTION));
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body(clientDescription);
	}

	@PostMapping(value = "/setSkypeLogin")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<String> setClientSkypeLogin(@RequestParam(name = "clientId") Long clientId,
													  @RequestParam(name = "skypeLogin") String skypeLogin,
													  @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(clientId);
		Client checkDuplicateLogin = clientService.getClientBySkype(skypeLogin);
		if (client == null) {
			logger.error("Client with id {} not found", clientId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("client not found or description is same");
		}
		if (checkDuplicateLogin != null && checkDuplicateLogin.getSkype().equals(skypeLogin)) {
			logger.error("client with this skype login already exists");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("client with this skype login already exists");
		}
		client.setSkype(skypeLogin);
		client.addHistory(clientHistoryService.createInfoHistory(userFromSession, client, ClientHistory.Type.ADD_LOGIN, skypeLogin));
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body(skypeLogin);
	}

	@GetMapping(value = "/message/info/{id}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<Message> getClientMessageInfoByID(@PathVariable("id") Long id) {
		return new ResponseEntity<>(messageService.get(id), HttpStatus.OK);
	}
}