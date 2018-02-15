package com.ewp.crm.controllers;

import com.ewp.crm.exceptions.client.ClientException;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class ClientController {
	@Autowired
	ClientService clientService;
	//TODO Определить везде view

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getall() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("users", clientService.getAllClients());
		return modelAndView;
	}

	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public void addUser(Client client) throws ClientException {
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
