package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/user/rest/call")
public class IPTelephonyRestController {

	private static Logger logger = LoggerFactory.getLogger(IPTelephonyRestController.class);

	private final IPService ipService;
	private final ClientService clientService;
	private final ClientHistoryService clientHistoryService;
	private final CallRecordService callRecordService;
	private final DownloadCallRecordService downloadCallRecordService;
	private final String voximplantHash;

	@Autowired
	public IPTelephonyRestController(IPService ipService,
									 ClientService clientService,
									 ClientHistoryService clientHistoryService,
									 CallRecordService callRecordService,
									 DownloadCallRecordService downloadCallRecordService) {
		this.ipService = ipService;
		this.clientService = clientService;
		this.clientHistoryService = clientHistoryService;
		this.callRecordService = callRecordService;
		this.downloadCallRecordService = downloadCallRecordService;
		this.voximplantHash = DigestUtils.md5DigestAsHex((ipService.getVoximplantUserLogin(ipService.getVoximplantLoginForWebCall()) + ":voximplant.com:" + ipService.getVoximplantPasswordForWebCall()).getBytes());
	}

	//Сервис voximplant обращается к нашему rest контроллеру и сетит ему запись разговора.
	//Не секьюритить
	@GetMapping(value = "/setCallRecord")
	public ResponseEntity setCallRecord(@RequestParam String url, @RequestParam Long clientCallId) {
		CallRecord callRecord = callRecordService.get(clientCallId);
		if (Optional.ofNullable(callRecord).isPresent()) {
			String downloadLink = downloadCallRecordService.downloadRecord(url, clientCallId, callRecord.getClientHistory().getId());
			callRecord.setLink(downloadLink);
			callRecord.getClientHistory().setRecordLink(url);
			callRecordService.update(callRecord);
			logger.info("CallRecord to client id {} has download", clientCallId);
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "/record/{file}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN, USER')")
	public byte[] getCallRecord(@PathVariable String file) throws IOException {
		Path fileLocation = Paths.get("CallRecords\\" + file + ".mp3");
		return Files.readAllBytes(fileLocation);
	}

	@GetMapping(value = "/voximplantCredentials")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN, USER')")
	public String getVoximplantCredentials() {
		return ipService.getVoximplantLoginForWebCall() + "," + ipService.getVoximplantPasswordForWebCall();
	}

	@PostMapping(value = "/voximplant")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN, USER')")
	public void voximplantCall(@RequestParam String from,
							   @RequestParam String to,
							   @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.getClientByPhoneNumber(to);
		if (client.isCanCall() && userFromSession.isIpTelephony()) {
			CallRecord callRecord = new CallRecord();
			ClientHistory clientHistory = clientHistoryService.createHistory(userFromSession, "http://www.google.com");
			ClientHistory historyFromDB = clientHistoryService.addHistory(clientHistory);
			client.addHistory(historyFromDB);
			callRecord.setClientHistory(historyFromDB);
			CallRecord callRecordFromDB = callRecordService.addCallRecord(callRecord);
			client.addCallRecord(callRecordFromDB);
			clientService.updateClient(client);
			callRecordFromDB.setClient(client);
			callRecordService.update(callRecordFromDB);
			ipService.call(from, to, callRecordFromDB.getId());
		}
	}

	@PostMapping(value = "/sendData")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN, USER')")
	public ResponseEntity getCallRecordsCredentials(@RequestParam String to,
													@AuthenticationPrincipal User userFromSession) {
		Client client = clientService.getClientByPhoneNumber(to);
		if (client.isCanCall() && userFromSession.isIpTelephony()) {
			CallRecord callRecord = new CallRecord();
			ClientHistory clientHistory = clientHistoryService.createHistory(userFromSession, "http://www.google.com");
			ClientHistory historyFromDB = clientHistoryService.addHistory(clientHistory);
			client.addHistory(historyFromDB);
			callRecord.setClientHistory(historyFromDB);
			CallRecord callRecordFromDB = callRecordService.addCallRecord(callRecord);
			client.addCallRecord(callRecordFromDB);
			clientService.updateClient(client);
			callRecordFromDB.setClient(client);
			callRecordService.update(callRecordFromDB);
			return ResponseEntity.ok(callRecordFromDB);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping(value = "/calcKey")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN, USER')")
	public String getHash(@RequestParam String key) {
		String hashKey = key + "|" + voximplantHash;
		return DigestUtils.md5DigestAsHex(hashKey.getBytes());
	}
}
