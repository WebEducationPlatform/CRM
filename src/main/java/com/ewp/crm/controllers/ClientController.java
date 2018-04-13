package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.EmailTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class ClientController {

	private static Logger logger = LoggerFactory.getLogger(ClientController.class);

	private final StatusService statusService;

	private final ClientService clientService;

	private final UserService userService;

	private final EmailTemplateService emailTemplateService;

	@Autowired
	public ClientController(StatusService statusService, ClientService clientService, UserService userService, EmailTemplateService emailTemplateService, ImageConfig imageConfig) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.userService = userService;
		this.emailTemplateService = emailTemplateService;
	}

	@RequestMapping(value = "/client",method = RequestMethod.GET)
	public ModelAndView getAll() {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Status> statuses;
		ModelAndView modelAndView = new ModelAndView("main-client-table");
		if (userFromSession.getRole().contains(new Role("ADMIN"))) {
			statuses = statusService.getAll();
			modelAndView.addObject("isAdmin", true);
		} else {
			statuses = statusService.getStatusesWithClientsForUser(userFromSession);
		}
		modelAndView.addObject("statuses", statuses);
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("users", userService.getAll());
		modelAndView.addObject("emailTmpl", emailTemplateService.getall());
		return modelAndView;
	}

	@RequestMapping(value = "/client/allClients", method = RequestMethod.GET)
	public ModelAndView allUsersPage() {
		ModelAndView modelAndView = new ModelAndView("all-clients-table");
		modelAndView.addObject("allClients", clientService.getAllClients());
		return modelAndView;
	}

	@RequestMapping(value = "/client/addClient", method = RequestMethod.POST)
	public void addUser(Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " добавил клиента"));
		clientService.addClient(client);
		logger.info("{} has added client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
	}

	@RequestMapping(value = "/admin/client/updateClient", method = RequestMethod.POST)
	public void updateUser(Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getFullName() + " изменил клиента"));
		clientService.updateClient(client);
		logger.info("{} has updated client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
	}

	@RequestMapping(value = "/admin/client/deleteClient", method = RequestMethod.POST)
	public void deleteUser(Client client) {
		clientService.deleteClient(client);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("{} has deleted client: id {}, email {}", currentAdmin.getFullName(), client.getId(), client.getEmail());
	}

	@RequestMapping(value = "/admin/client/clientInfo/{id}", method = RequestMethod.GET)
	public ModelAndView clientInfo(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView("client-info");
		modelAndView.addObject("client", clientService.getClientByID(id));
		modelAndView.addObject("states", Client.State.values());
		modelAndView.addObject("socialMarkers", SocialNetwork.SocialMarker.values());
		return modelAndView;
	}
}
