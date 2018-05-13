package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import com.ewp.crm.service.interfaces.StatusService;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	@Autowired
	public ClientRestController(ClientService clientService, SocialNetworkTypeService socialNetworkTypeService, UserService userService, ClientHistoryService clientHistoryService, StatusService statusService) {
		this.clientService = clientService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.userService = userService;
		this.clientHistoryService = clientHistoryService;
		this.statusService = statusService;
	}

	@RequestMapping(value = "/rest/client", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAllClients());
	}

	@RequestMapping(value = "/rest/client/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.getClientByID(id));
	}

	@RequestMapping(value = "/rest/client/assign", method = RequestMethod.POST)
	public ResponseEntity<User> assign(@RequestParam(name = "clientId") Long clientId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		if (client.getOwnerUser() != null) {
			logger.info("User {} tried to assign a client with id {}, but client have owner", user.getEmail(), clientId);
			return ResponseEntity.badRequest().body(null);
		}
		client.setOwnerUser(user);
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {}", user.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	//TODO hasAnyAuthority('ADMIN')
	@RequestMapping(value = "/rest/client/assign/user", method = RequestMethod.POST)
	public ResponseEntity<User> assignUser(@RequestParam(name = "clientId") Long clientId,
	                                       @RequestParam(name = "userForAssign") Long userId) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User assignUser = userService.get(userId);
		Client client = clientService.getClientByID(clientId);
		if (client.getOwnerUser() != null && client.getOwnerUser().equals(assignUser)) {
			logger.info("User {} tried to assign a client with id {}, but client have same owner {}", principal.getEmail(), clientId, assignUser.getEmail());
			return ResponseEntity.ok(client.getOwnerUser());
		}
		client.setOwnerUser(assignUser);
		clientService.updateClient(client);
		logger.info("User {} has assigned client with id {} to user {}", principal.getEmail(), clientId, assignUser.getEmail());
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@RequestMapping(value = "/rest/client/unassign", method = RequestMethod.POST)
	public ResponseEntity unassign(@RequestParam(name = "clientId") Long clientId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		if (client.getOwnerUser() == null) {
			logger.info("User {} tried to unassign a client with id {}, but client already doesn't have owner", user.getEmail(), clientId);
			return ResponseEntity.badRequest().build();
		}
		client.setOwnerUser(null);
		clientService.updateClient(client);
		logger.info("User {} has unassigned client with id {}", user.getEmail(), clientId);
		return ResponseEntity.ok(client.getOwnerUser());
	}

	@RequestMapping(value = "/admin/rest/client/update", method = RequestMethod.POST)
	public ResponseEntity updateClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (SocialNetwork socialNetwork : client.getSocialNetworks()) {
			socialNetwork.getSocialNetworkType().setId(socialNetworkTypeService.getByTypeName(
					socialNetwork.getSocialNetworkType().getName()).getId());
		}
		Client clientFromDB = clientService.getClientByID(currentClient.getId());
		currentClient.setHistory(clientFromDB.getHistory());
		currentClient.setComments(clientFromDB.getComments());
		currentClient.setOwnerUser(clientFromDB.getOwnerUser());
		currentClient.setStatus(clientFromDB.getStatus());
		currentClient.setDateOfRegistration(clientFromDB.getDateOfRegistration());
		currentClient.setSmsInfo(clientFromDB.getSmsInfo());
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.UPDATE_CLIENT, currentAdmin);
		clientHistoryService.generateValidHistory(clientHistory, currentClient);
		currentClient.addHistory(clientHistory);
		clientService.updateClient(currentClient);
		logger.info("{} has updated client: id {}, email {}", currentAdmin.getFullName(), currentClient.getId(), currentClient.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/rest/client/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity deleteClient(@PathVariable Long id) {
		clientService.deleteClient(id);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("{} has deleted client with id {}", currentAdmin.getFullName(), id);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/rest/client/add", method = RequestMethod.POST)
	public ResponseEntity addClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (SocialNetwork socialNetwork : client.getSocialNetworks()) {
			socialNetwork.getSocialNetworkType().setId(socialNetworkTypeService.getByTypeName(
					socialNetwork.getSocialNetworkType().getName()).getId());
		}
		client.setDateOfRegistration(LocalDateTime.now().toDate());
		Status status = statusService.get(client.getStatus().getName());
		client.setStatus(status);
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.ADD_CLIENT, principal);
		clientHistoryService.generateValidHistory(clientHistory, client);
		client.addHistory(clientHistory);
		clientService.addClient(client);
		logger.info("{} has added client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/client/filtration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAllWithConditions(@RequestBody FilteringCondition filteringCondition) {
		List<Client> clients = clientService.filteringClient(filteringCondition);
		return ResponseEntity.ok(clients);
	}

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
					bufferedWriter.write(email + "\r\n");
				}
			}
			if (selected.equals("phoneNumber")) {
				List<String> phoneNumbers = clientService.getClientsPhoneNumbers();
				for (String phoneNumber : phoneNumbers) {
					bufferedWriter.write(phoneNumber + "\r\n");
				}
			}

			bufferedWriter.close();
		} catch (IOException e) {
			logger.error("File not created!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

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
					logger.error("File for filtered clients not created!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


			List<Client> clients = clientService.filteringClient(filteringCondition);

			if (Optional.ofNullable(socialNetworkTypeService.getByTypeName(filteringCondition.getSelected())).isPresent()) {
				List<String> socialNetworkLinks = clientService.getFilteredClientsSNLinks(filteringCondition);
				for (String socialNetworkLink : socialNetworkLinks) {
					bufferedWriter.write(socialNetworkLink + "\r\n");
				}
			}
			if (filteringCondition.getSelected().equals("email")) {
				List<String> emails = clientService.getFilteredClientsEmail(filteringCondition);
				for (String email : emails) {
					bufferedWriter.write(email + "\r\n");
				}
			}
			if (filteringCondition.getSelected().equals("phoneNumber")) {
				for (Client client : clients) {
					bufferedWriter.write(client.getPhoneNumber() + "\r\n");
				}
			}

			bufferedWriter.close();
		} catch (IOException e) {
			logger.error("File not created!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "rest/client/getClientsData", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getClientsData() {

		String path = "DownloadData\\";
		File file = new File(path + "data.txt");

		InputStreamResource resource = null;
		try {
			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("File not found!");
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment;filename=" + file.getName())
				.contentType(MediaType.TEXT_PLAIN).contentLength(file.length())
				.body(resource);

	}

	@RequestMapping(value = "admin/rest/client/postpone", method = RequestMethod.POST)
	public ResponseEntity postponeClient(@RequestParam Long clientId, @RequestParam String date) {
		try {
			Client client = clientService.getClientByID(clientId);
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm");
			LocalDateTime postponeDate = LocalDateTime.parse(date, dateTimeFormatter);
			if (postponeDate.isBefore(LocalDateTime.now()) || postponeDate.isEqual(LocalDateTime.now())) {
				logger.info("Wrong postpone date: {}", date);
				return ResponseEntity.badRequest().body("Дата должна быть позже текущей даты");
			}
			client.setPostponeDate(postponeDate.toDate());
			User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			client.addHistory(clientHistoryService.generateValidHistory(new ClientHistory(ClientHistory.Type.POSTPONE, principal), client));
			clientService.updateClient(client);
			logger.info("{} has postponed client id:{} until {}", principal.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}

	@RequestMapping(value = "rest/client/addDescription", method = RequestMethod.POST)
	public ResponseEntity<String> addDescription(@RequestParam(name = "clientId") Long clientId,
	                                             @RequestParam(name = "clientDescription") String clientDescription) {
		Client client = clientService.getClientByID(clientId);
		if (client == null) {
			logger.error("Can`t add description, client with id {} not found", clientId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("client not found");
		}
		client.setClientDescriptionComment(clientDescription);
		clientService.updateClient(client);
		return ResponseEntity.status(HttpStatus.OK).body(clientDescription);
	}
}