package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.dto.ClientHistoryDto;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/client/history")
public class ClientHistoryRestController {

	private static final Logger logger = LoggerFactory.getLogger(ClientHistoryRestController.class);

	private final ClientHistoryService clientHistoryService;

	@Value("${project.pagination.page-size.client-history}")
	private int pageSize;

	@Autowired
	public ClientHistoryRestController(ClientHistoryService clientHistoryService) {
		this.clientHistoryService = clientHistoryService;
	}

	@GetMapping("/getHistory/{clientId}")
	public ResponseEntity getClientHistory(@PathVariable("clientId") long id, @RequestParam("page")int page, @RequestParam("isAsc")boolean isAsc) {
		List<ClientHistoryDto> clientHistory = clientHistoryService.getAllDtoByClientId(id, page, pageSize, isAsc);
		return ResponseEntity.ok(clientHistory);
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
