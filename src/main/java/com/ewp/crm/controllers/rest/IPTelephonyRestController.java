package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.CallRecord;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Optional;

@RestController
@RequestMapping("/user/rest/call")
public class IPTelephonyRestController {

	public static final String INIT_RECORD_LINK = "http://www.google.com";
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
		String loginForWebCall = ipService.getVoximplantLoginForWebCall().isPresent() ? ipService.getVoximplantLoginForWebCall().get() : "";
		String userLogin = ipService.getVoximplantUserLogin(loginForWebCall).isPresent() ? ipService.getVoximplantUserLogin(loginForWebCall).get() : "";
		String pass = ipService.getVoximplantPasswordForWebCall().isPresent() ? ipService.getVoximplantPasswordForWebCall().get() : "";
		this.voximplantHash = DigestUtils.md5DigestAsHex((userLogin + ":voximplant.com:" + pass).getBytes());
	}

	//Сервис voximplant обращается к нашему rest контроллеру и сетит ему запись разговора.
	//Не секьюритить
	@GetMapping(value = "/setCallRecord")
	public ResponseEntity setCallRecord(@RequestParam String url, @RequestParam Long clientCallId,
										@RequestParam String code) {
		String codeOpt = ipService.getVoximplantCodeToSetRecord().isPresent() ? ipService.getVoximplantCodeToSetRecord().get() : "";
		if (code != null && !code.equals(codeOpt)) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		CallRecord callRecord = callRecordService.get(clientCallId);
		if (Optional.ofNullable(callRecord).isPresent()) {
			Optional<String> downloadLink = downloadCallRecordService.downloadRecord(url, clientCallId, callRecord.getClientHistory().getId());
			if (downloadLink.isPresent()) {
				callRecord.setLink(downloadLink.get());
				callRecord.getClientHistory().setLink(url);
				callRecord.getClientHistory().setRecordLink(downloadLink.get());
				callRecordService.update(callRecord);
				logger.info("CallRecord to client id {} has download", clientCallId);
			} else {
				logger.info("Can't download CallRecord to client id {}: no download link present", clientCallId);
			}
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "/record/{file}")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<FileSystemResource> getCallRecord(@PathVariable String file) {
		File fileLocation = new File("CallRecords/" + file + ".mp3");
		if (fileLocation.exists()) {
			return new ResponseEntity<>(new FileSystemResource(fileLocation), HttpStatus.OK);
		} else {
			logger.error("File with record not found: " + fileLocation.toString());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping(value = "/voximplantCredentials")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public String getVoximplantCredentials() {
		if (ipService.getVoximplantLoginForWebCall().isPresent() && ipService.getVoximplantPasswordForWebCall().isPresent()) {
			return ipService.getVoximplantLoginForWebCall().get() + "," + ipService.getVoximplantPasswordForWebCall().get();
		} else {
			return "";
		}
	}

	@PostMapping(value = "/voximplant")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public void voximplantCall(@RequestParam String from,
							   @RequestParam String to,
							   @AuthenticationPrincipal User userFromSession) {
		Optional<Client> client = clientService.getClientByPhoneNumber(to);
		if (client.isPresent() && client.get().isCanCall() && userFromSession.isIpTelephony()) {
			CallRecord callRecord = new CallRecord();
			Optional<ClientHistory> clientHistory = clientHistoryService.createHistory(userFromSession, INIT_RECORD_LINK);
			if (clientHistory.isPresent()) {
				Optional<ClientHistory> historyFromDB = clientHistoryService.addHistory(clientHistory.get());
				if (historyFromDB.isPresent()) {
					client.get().addHistory(historyFromDB.get());
					callRecord.setClientHistory(historyFromDB.get());
					Optional<CallRecord> callRecordFromDB = callRecordService.addCallRecord(callRecord);
					if (callRecordFromDB.isPresent()) {
						client.get().addCallRecord(callRecordFromDB.get());
						clientService.updateClient(client.get());
						callRecordFromDB.get().setClient(client.get());
						callRecordService.update(callRecordFromDB.get());
						ipService.call(from, to, callRecordFromDB.get().getId());
					}
				}
			}
		}
	}

	@PostMapping(value = "/sendData")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity getCallRecordsCredentials(@RequestParam String to,
													@AuthenticationPrincipal User userFromSession) {
		Optional<Client> client = clientService.getClientByPhoneNumber(to);
		if (client.isPresent() && client.get().isCanCall() && userFromSession.isIpTelephony()) {
			CallRecord callRecord = new CallRecord();
			Optional<ClientHistory> clientHistory = clientHistoryService.createHistory(userFromSession, "http://www.google.com");
			if (clientHistory.isPresent()) {
				Optional<ClientHistory> historyFromDB = clientHistoryService.addHistory(clientHistory.get());
				if (historyFromDB.isPresent()) {
					client.get().addHistory(historyFromDB.get());
					callRecord.setClientHistory(historyFromDB.get());
					Optional<CallRecord> callRecordFromDB = callRecordService.addCallRecord(callRecord);
					if (callRecordFromDB.isPresent()) {
						client.get().addCallRecord(callRecordFromDB.get());
						clientService.updateClient(client.get());
						callRecordFromDB.get().setClient(client.get());
						callRecordService.update(callRecordFromDB.get());
						return ResponseEntity.ok(callRecordFromDB.get());
					}
				}
			}
		}
		return ResponseEntity.badRequest().build();
	}

	@PostMapping(value = "/calcKey")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public String getHash(@RequestParam String key) {
		String hashKey = key + "|" + voximplantHash;
		return DigestUtils.md5DigestAsHex(hashKey.getBytes());
	}
}
