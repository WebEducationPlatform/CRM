package com.ewp.crm.controllers;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class ClientController {

	private static Logger logger = LoggerFactory.getLogger(ClientController.class);

	private final StatusService statusService;

	private final ClientService clientService;

	private final UserService userService;

	private final EmailTemplateService emailTemplateService;

	private final SocialNetworkTypeService socialNetworkTypeService;

	private final NotificationService notificationService;

	@Autowired
	public ClientController(StatusService statusService, ClientService clientService, UserService userService,
	                        EmailTemplateService emailTemplateService, SocialNetworkTypeService socialNetworkTypeService, NotificationService notificationService) {
		this.statusService = statusService;
		this.clientService = clientService;
		this.userService = userService;
		this.emailTemplateService = emailTemplateService;
		this.socialNetworkTypeService = socialNetworkTypeService;
		this.notificationService = notificationService;
	}

	@RequestMapping(value = "/client", method = RequestMethod.GET)
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
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("statuses", statuses);
		modelAndView.addObject("users", userService.getAll());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		modelAndView.addObject("emailTmpl", emailTemplateService.getall());
		return modelAndView;
	}

	@RequestMapping(value = "/client/allClients", method = RequestMethod.GET)
	public ModelAndView allUsersPage() {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView modelAndView = new ModelAndView("all-clients-table");
		modelAndView.addObject("allClients", clientService.getAllClients());
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
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
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView modelAndView = new ModelAndView("client-info");
		modelAndView.addObject("client", clientService.getClientByID(id));
		modelAndView.addObject("states", Client.State.values());
		modelAndView.addObject("socialMarkers", socialNetworkTypeService.getAll());
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));

		return modelAndView;
	}


	@RequestMapping(value = "/admin/client/add", method = RequestMethod.GET)
	public ModelAndView addClient() {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView modelAndView = new ModelAndView("add-client");
		modelAndView.addObject("states", Client.State.values());
		modelAndView.addObject("socialMarkers", socialNetworkTypeService.getAll());
		modelAndView.addObject("user", userFromSession);
		modelAndView.addObject("notifications", notificationService.getByUserToNotify(userFromSession));
		return modelAndView;
	}

	@RequestMapping(value = "/phone", method = RequestMethod.GET)
	public ModelAndView getPhone() {
		return new ModelAndView("webrtrc");
	}
}
