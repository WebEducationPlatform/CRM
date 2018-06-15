package com.ewp.crm.controllers.rest;


import com.ewp.crm.component.util.interfaces.IPUtil;
import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.CallRecordService;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.DownloadCallRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
/*
	Сервис voximplant обращается к нашему rest контроллеру и сетит ему запись разговора.
	Не секьюритить
 */
@RequestMapping("/user/rest/call")
public class IPTelephonyRestController {

	private final IPUtil ipUtil;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;
	private final CallRecordService callRecordService;
	private final DownloadCallRecordService downloadCallRecordService;
	private static Logger logger = LoggerFactory.getLogger(IPTelephonyRestController.class);


	@Autowired
	public IPTelephonyRestController(IPUtil ipUtil, ClientService clientService, ClientHistoryService clientHistoryService, CallRecordService callRecordService, DownloadCallRecordService downloadCallRecordService) {
		this.ipUtil = ipUtil;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
		this.callRecordService = callRecordService;
		this.downloadCallRecordService = downloadCallRecordService;
	}

	@PreAuthorize("hasAnyAuthority('ADMIN, USER')")
	@RequestMapping(value = "/voximplant", method = RequestMethod.POST)
	public void voximplantCall(@RequestParam String from, @RequestParam String to) {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByPhoneNumber(to);
		if (client.isCanCall() && principal.isIpTelephony()) {
			CallRecord callRecord = new CallRecord();
			ClientHistory clientHistory = clientHistoryService.createHistory(principal, null);
			ClientHistory historyFromDB = clientHistoryService.addHistory(clientHistory);
			client.addHistory(historyFromDB);
			callRecord.setClientHistory(historyFromDB);
			CallRecord callRecordFromDB = callRecordService.add(callRecord);
			client.addCallRecord(callRecordFromDB);
			clientService.updateClient(client);
			ipUtil.call(from, to, callRecordFromDB.getId());
		}
	}

	@RequestMapping(value = "/setCallRecord", method = RequestMethod.GET)
	public ResponseEntity setCallRecord(@RequestParam String url, @RequestParam Long clientCallId) {
		CallRecord callRecord = callRecordService.getCallRecord(clientCallId);
		if (Optional.ofNullable(callRecord).isPresent()) {
			String downloadLink = downloadCallRecordService.downloadRecord(url, clientCallId, callRecord.getClientHistory().getId());
			callRecord.setLink(downloadLink);
			callRecord.getClientHistory().setRecordLink(downloadLink);
			callRecordService.update(callRecord);
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN, USER')")
	@ResponseBody
	@RequestMapping(value = "/record/{file}", method = RequestMethod.GET)
	public byte[] getCallRecord(@PathVariable String file) throws IOException {
		Path fileLocation = Paths.get("CallRecords\\" + file + ".mp3");
		return Files.readAllBytes(fileLocation);
	}
}
