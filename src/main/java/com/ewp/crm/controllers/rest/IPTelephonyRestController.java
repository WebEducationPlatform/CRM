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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
	@Value("${project.pagination.page-size.client-history}")
	private int pageSize;

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
	//Не вешать Security!
	@GetMapping(value = "/setCallRecord")
	public ResponseEntity setCallRecord(@RequestParam String url, @RequestParam(required = false) Optional<Long> clientCallId,
										@RequestParam(required = false) Optional<Long> commonCallId, @RequestParam String code) {
		String codeOpt = ipService.getVoximplantCodeToSetRecord().isPresent() ? ipService.getVoximplantCodeToSetRecord().get() : "";
		if (code != null && !code.equals(codeOpt)) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		if (clientCallId.isPresent()) {
			CallRecord callRecord = callRecordService.get(clientCallId.get());
			if (Optional.ofNullable(callRecord).isPresent()) {
				Optional<String> downloadLink = downloadCallRecordService.getRecordLink(url, clientCallId.get(), callRecord.getClientHistory().getId());
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
		} else if (commonCallId.isPresent()) {
			CallRecord callRecord = callRecordService.get(commonCallId.get());
			if (Optional.ofNullable(callRecord).isPresent()) {
				Optional<String> downloadLink = downloadCallRecordService.getRecordLink(url, commonCallId.get());
				if (downloadLink.isPresent()) {
					callRecord.setLink(downloadLink.get());
					callRecordService.update(callRecord);
					logger.info("CallRecord with id {} has been downloaded");
				} else {
					logger.info("Can't download CallRecord with id {}: no download link present", commonCallId);
				}
			}
		} else {
			CallRecord callRecord = new CallRecord();
			Optional<CallRecord> callRecordFromDB = callRecordService.addCallRecord(callRecord);
			if (callRecordFromDB.isPresent()) {
				Optional<String> downloadLink = downloadCallRecordService.getRecordLink(url, callRecordFromDB.get().getId());
				if (downloadLink.isPresent()) {
					callRecordFromDB.get().setLink(downloadLink.get());
					callRecordService.update(callRecordFromDB.get());
					logger.info("CallRecord with id {} has been downloaded");
				} else {
					logger.info("Can't download CallRecord with id {}: no download link present", commonCallId);
				}
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

	@GetMapping(value = "/records/all")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity getAllCommonRecords(@RequestParam("page") int page) {
		Pageable pageable = PageRequest.of(page, pageSize);
		List<CallRecord> callRecords = callRecordService.getAllCommonRecords(pageable);
		if (callRecords == null || callRecords.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(callRecords);
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

	@PostMapping(value = "/toClient")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity getCallRecordToClientCredentials(@RequestParam String to, @AuthenticationPrincipal User userFromSession) {
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
						return ResponseEntity.ok(callRecordFromDB.get());
					}
				}
			}
		}
		return ResponseEntity.badRequest().build();
	}

	@PostMapping(value = "/common")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity getCallRecordsCredentials(@RequestParam String to, @AuthenticationPrincipal User userFromSession) {
		if (userFromSession.isIpTelephony()) {
			CallRecord callRecord = new CallRecord();
			Optional<CallRecord> callRecordFromDB = callRecordService.addCallRecord(callRecord, userFromSession, to);
			if (callRecordFromDB.isPresent()) {
				return ResponseEntity.ok(callRecordFromDB.get());
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
