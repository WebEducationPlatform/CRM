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
		//DEMO link
		String link = "http://storage-ru1.voximplant.com/ru12-records/2018/05/15/ODhlZGNjN2RlZGQxMGY3MmRlZTQwZjZlYzUzN2U0NTYvaHR0cDovL3d3dy1ydS0xMi0yNDgudm94aW1wbGFudC5jb20vcmVjb3Jkcy8yMDE4LzA1LzE1LzMzNjI0OWY3MTYxN2FhY2IuMTUyNjM4NjMzOC40MTc1ODEubXAz?record_id=53739491&account_email=sevostyanovg.d@gmail.com&session_id=9af21ca330cb4a1d804652ce7851b6be&_ga=2.195922092.1800083058.1526191182-1386242328.1524756784";
		Client client = clientService.getClientByPhoneNumber(to);
		client.addHistory(clientHistoryService.createHistory(principal, client, ClientHistory.Type.CALL, link));
		clientService.updateClient(client);
	}
}
