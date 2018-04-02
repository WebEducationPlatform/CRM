package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.io.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin/rest/client")
public class RestClientController {

	private static Logger logger = LoggerFactory.getLogger(RestClientController.class);

	private final ClientService clientService;

	@Autowired
	public RestClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAll() {
		return ResponseEntity.ok(clientService.getAllClients());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Client> getClientByID(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.getClientByID(id));
	}

	@RequestMapping(value = "/assign", method = RequestMethod.POST)
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

	@RequestMapping(value = "/unassign", method = RequestMethod.POST)
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

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity updateClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.setHistory(clientService.getClientByID(client.getId()).getHistory());
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " изменил профиль клиента"));
		clientService.updateClient(client);
		logger.info("{} has updated client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity deleteClient(@PathVariable Long id) {
		clientService.deleteClient(id);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("{} has deleted client with id {}", currentAdmin.getFullName(), id);
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/addClient", method = RequestMethod.POST)
	public ResponseEntity addClient(@RequestBody Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " добавил клиента"));
		clientService.addClient(client);
		logger.info("{} has added client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/filtration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAllWithConditions(@RequestBody FilteringCondition filteringCondition) {
		return ResponseEntity.ok(clientService.filteringClient(filteringCondition));
	}

	@RequestMapping(value = "/createFile", method = RequestMethod.POST)
	public ResponseEntity createFile(
			@RequestParam(name = "selected") String selected) {

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
			logger.error("Файл не создан!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/createFileFiltr", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
			logger.error("Файл не создан!");
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@RequestMapping(value = "/getClientsData", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getClientsData() {

		String path = "src/main/resources/clientData/";
		File file = new File(path + "data.txt");

		InputStreamResource resource = null;
		try {

			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment;filename=" + file.getName())
				.contentType(MediaType.TEXT_PLAIN).contentLength(file.length())
				.body(resource);

	}

}