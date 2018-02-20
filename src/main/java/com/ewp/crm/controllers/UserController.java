package com.ewp.crm.controllers;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/client")
public class UserController {

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

	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public void addUser(Client client) {
		clientService.addClient(client);
	}

	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public void updateUser(Client client) {
		clientService.updateClient(client);
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public void deleteUser(Client client) {
		clientService.deleteClient(client);
	}
}
