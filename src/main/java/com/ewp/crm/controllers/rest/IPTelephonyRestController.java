package com.ewp.crm.controllers.rest;


import com.ewp.crm.component.util.interfaces.IPUtil;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/rest/call")
public class IPTelephonyRestController {

	private final IPUtil ipUtil;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;

	@Autowired
	public IPTelephonyRestController(IPUtil ipUtil, ClientService clientService,ClientHistoryService clientHistoryService) {
		this.ipUtil = ipUtil;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
	}

	@RequestMapping(value = "/voximplant", method = RequestMethod.POST)
	public void voximplantCall(@RequestParam String from, @RequestParam String to) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ipUtil.call(from, to);
		Client client = clientService.getClientByPhoneNumber(to);
		ClientHistory clientHistory = new ClientHistory(ClientHistory.Type.CALL, principal, "#");
		client.addHistory(clientHistoryService.generateValidHistory(clientHistory, client));
		clientService.updateClient(client);
	}
}
