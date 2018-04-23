package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import java.util.List;
import java.util.Optional;

@RestController
public class ClientRestController {

	private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

	private final ClientService clientService;
	private final SocialNetworkTypeService socialNetworkTypeService;

	@Autowired
	public ClientRestController(ClientService clientService, SocialNetworkTypeService socialNetworkTypeService) {
		this.clientService = clientService;
		this.socialNetworkTypeService = socialNetworkTypeService;
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
		Client currentClient = clientService.getClientByID(client.getId());
		client.setHistory(currentClient.getHistory());
		client.setComments(currentClient.getComments());
		client.setOwnerUser(currentClient.getOwnerUser());
		client.setStatus(currentClient.getStatus());
		client.setDateOfRegistration(currentClient.getDateOfRegistration());
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " изменил профиль клиента"));
		clientService.updateClient(client);
		logger.info("{} has updated client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/rest/client/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity deleteClient(@PathVariable Long id) {
		clientService.deleteClient(id);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("{} has deleted client with id {}", currentAdmin.getFullName(), id);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/client/addClient", method = RequestMethod.POST)
	public ResponseEntity addClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " добавил клиента"));
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

		String path = "src/main/resources/clientData/";
		File file = new File(path + "data.txt");

		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			List<Client> clients = clientService.getAllClients();
			if (selected.equals("vk") || selected.equals("facebook")) {
				for (Client client : clients) {
					List<SocialNetwork> socialNetworks = client.getSocialNetworks();
					for (SocialNetwork socialNetwork : socialNetworks) {
						if (Optional.ofNullable(socialNetwork).isPresent()) {
							if (socialNetwork.getSocialNetworkType().getName().equals(selected)) {
								bufferedWriter.write(socialNetwork.getLink() + "\r\n");
							}
						}
					}
				}
			}
			if (selected.equals("email")) {
				for (Client client : clients) {
					bufferedWriter.write(client.getEmail() + "\r\n");
				}
			}
			if (selected.equals("phoneNumber")) {
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

	@RequestMapping(value = "rest/client/createFileFiltr", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity createFileWithFiltr(@RequestBody FilteringCondition filteringCondition) {
		String path = "src/main/resources/clientData/";
		File file = new File(path + "data.txt");

		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


			List<Client> clients = clientService.filteringClient(filteringCondition);
			if (filteringCondition.getSelected().equals("vk") || filteringCondition.getSelected().equals("facebook")) {
				for (Client client : clients) {
					List<SocialNetwork> socialNetworks = client.getSocialNetworks();
					for (SocialNetwork socialNetwork : socialNetworks) {
						if (Optional.ofNullable(socialNetwork).isPresent()) {
							if (socialNetwork.getSocialNetworkType().getName().equals(filteringCondition.getSelected())) {
								bufferedWriter.write(socialNetwork.getLink() + "\r\n");
							}
						}
					}
				}
			}
			if (filteringCondition.getSelected().equals("email")) {
				for (Client client : clients) {
					bufferedWriter.write(client.getEmail() + "\r\n");
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

		String path = "src/main/resources/clientData/";
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
			User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			client.addHistory(new ClientHistory(currentAdmin.getFullName() + " скрыл клиента до " + date));
			clientService.updateClient(client);
			logger.info("{} has postponed client id:{} until {}", currentAdmin.getFullName(), client.getId(), date);
			return ResponseEntity.ok(HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Произошла ошибка");
		}
	}
}