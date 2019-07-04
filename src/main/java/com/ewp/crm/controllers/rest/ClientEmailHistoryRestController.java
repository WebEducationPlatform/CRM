package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/client/history/rest")
public class ClientEmailHistoryRestController {

	private final ClientHistoryService clientHistoryService;

	@Autowired
	public ClientEmailHistoryRestController(ClientHistoryService clientHistoryService) {
		this.clientHistoryService = clientHistoryService;
	}

	@GetMapping("/getEmailHistory/{clientId}")
	public ResponseEntity getClientEmailMessages(@PathVariable("clientId") long id) {
		List<ClientHistory> clientHistories = clientHistoryService.getByClientId(id);
		clientHistories.removeIf(x -> !x.getType().name().equals("SEND_MESSAGE"));
		List<String[]> response = new ArrayList<>();
		for (ClientHistory clientHistory: clientHistories) {
			String time = clientHistory.getDate().toString();
			String author = clientHistory.getMessage().getAuthorName();
			if (author == null || author.isEmpty()) {
				author = "неопределен";
			}
			String content = clientHistory.getMessage().getContent();
			String[] messageData = {time, author, content};
			response.add(messageData);
		}
		return ResponseEntity.ok(response);
	}

}
