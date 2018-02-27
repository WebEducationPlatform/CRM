package com.ewp.crm.controllers;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/client")
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	private final StatusService statusService;

	private final ClientService clientService;

	@Autowired
	public UserController(StatusService statusService, ClientService clientService) {
		this.statusService = statusService;
		this.clientService = clientService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getAll() {
		ModelAndView modelAndView = new ModelAndView("main-client-table");
		modelAndView.addObject("allStatuses", statusService.getAll());
		return modelAndView;
	}

	@RequestMapping(value = "/allClients", method = RequestMethod.GET)
	public ModelAndView allUsersPage() {
		ModelAndView modelAndView = new ModelAndView("all-clients-table");
		modelAndView.addObject("allClients", clientService.getAllClients());
		return modelAndView;
	}

	@RequestMapping(value = "/addClient", method = RequestMethod.POST)
	public void addUser(Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getEmail() + " добавил клиента"));
		clientService.addClient(client);
		logger.info("Admin {} has added client: id {}, email {}", currentAdmin.getEmail(), client.getId(), client.getEmail());
	}

	@RequestMapping(value = "/updateClient", method = RequestMethod.POST)
	public void updateUser(Client client) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		client.addHistory(new ClientHistory(currentAdmin.getEmail() + " изменил клиента"));
		clientService.updateClient(client);
		logger.info("Admin {} has updated client: id {}, email {}", currentAdmin.getEmail(), client.getId(), client.getEmail());
	}

	@RequestMapping(value = "/deleteClient", method = RequestMethod.POST)
	public void deleteUser(Client client) {
		clientService.deleteClient(client);
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.info("Admin {} has deleted client: id {}, email {}", currentAdmin.getEmail(), client.getId(), client.getEmail());
	}
}
