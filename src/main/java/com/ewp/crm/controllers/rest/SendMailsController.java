package com.ewp.crm.controllers.rest;

import com.ewp.crm.exceptions.parse.ParseMailingDataException;
import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ImageUploadDto;
import com.ewp.crm.service.email.MailingService;
import com.ewp.crm.service.interfaces.MailingMessageService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.UserService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
@PropertySource("file:./ckeditor.properties")
public class SendMailsController {

    private static Logger logger = LoggerFactory.getLogger(SendMailsController.class);
    private final MailingMessageService mailingMessageSendService;
    private final MailingService mailingService;
    private final UserService userService;
    private final SlackService slackService;
    private Environment environment;

    @Autowired
    public SendMailsController(MailingMessageService mailingMessageSendService, MailingService mailingService,
                               UserService userService, Environment environment, SlackService slackService) {
        this.mailingMessageSendService = mailingMessageSendService;
        this.mailingService = mailingService;
        this.userService = userService;
        this.slackService = slackService;
        this.environment = environment;
    }

    @PostMapping(value = "/client/mailing/send")
    public ResponseEntity<String> sendMails(@RequestParam("type") String type,
                                            @RequestParam("recipients") String recipients,
                                            @RequestParam("text") String text,
                                            @RequestParam("date") String date,
                                            @RequestParam("vkType") String vkType,
                                            @RequestParam("selectValueAppNumberToken") String selectAppNumberToToken,
                                            @AuthenticationPrincipal User userFromSession) {
        Optional<User> user = userService.getUserByEmail(userFromSession.getEmail());
        if (user.isPresent()) {
            try {
//                slackService.setAppToken(selectAppNumberToToken, environment);
                mailingService.prepareAndSendMailingMessages(type, recipients, text, date, vkType, user.get());
            } catch (ParseMailingDataException e) {
                logger.error("Incorrect data at send mailing request", e);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok("");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Value("${ckeditor.img.upload.path}")
    String uploadPath;
    @Value("${ckediror.img.uri}")
    String uploadUri;

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @PostMapping(value = "/image/upload", produces = "application/json")
    public ResponseEntity<ImageUploadDto> upload(@RequestPart MultipartFile upload, HttpServletRequest request) throws IOException {

        String sourceName = upload.getOriginalFilename();
        String sourceExt = FilenameUtils.getExtension(sourceName).toLowerCase();

        File destFile;
        String destFileName;

        String absolutePath = System.getProperty("user.dir");

        destFileName = System.currentTimeMillis()+"."+sourceExt;
        destFile = new File(absolutePath+FilenameUtils.separatorsToSystem("/"+uploadPath)+destFileName);

        destFile.getParentFile().mkdirs();

        upload.transferTo(destFile);
        URI imgUrl = URI.create(request.getScheme()+"://"+request.getServerName()+":"+request.getLocalPort()+uploadUri+destFileName);

        ImageUploadDto imageUploadDto = new ImageUploadDto(destFileName, imgUrl);

        return new ResponseEntity<>(imageUploadDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @GetMapping(value = "/mailing/history", produces = "application/json")
    public ResponseEntity<List<MailingMessage>> getHistoryMail(@AuthenticationPrincipal User userFromSession) {
        if (userFromSession.getRole().contains("OWNER")) {
            return ResponseEntity.ok(mailingMessageSendService.getAll());
        }
        return ResponseEntity.ok(mailingMessageSendService.getMailingMessageByUserId(userFromSession.getId()));
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @PostMapping("/mailing/manager/history")
    public ResponseEntity<List<MailingMessage>> getHistoryMailForManager(@RequestParam("managerId") Long id,
                                                                         @RequestParam("managerFromTime") String timeFrom,
                                                                         @RequestParam("managerToTime") String timeTo) {
        List<MailingMessage> list;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        //Проверка выбран ли пользователь для сортировки,
        //если ид пришел то выбираются сообщения отправленные данным пользователем
        //иначе выбираются все сообщения отправленые в выбраный интервал отправки
        if (id != null) {
            LocalDateTime destinationDateFrom = LocalDate.parse(timeFrom, dateTimeFormatter).atStartOfDay();
            LocalDateTime destinationDateTo = LocalDate.parse(timeTo, dateTimeFormatter).atStartOfDay();
            list = mailingMessageSendService.getMailingMessageByUserIdAndDate(id, destinationDateFrom, destinationDateTo);
        } else {
            LocalDateTime destinationDateFrom = LocalDate.parse(timeFrom, dateTimeFormatter).atStartOfDay();
            LocalDateTime destinationDateTo = LocalDate.parse(timeTo, dateTimeFormatter).atStartOfDay();
            list = mailingMessageSendService.getMailingMessageByDate(destinationDateFrom, destinationDateTo);
        }
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @GetMapping(value = "/get/sender")
    public ResponseEntity<List<User>> getVkTokenSender(@AuthenticationPrincipal User userFromSession) {
        if (userFromSession.getRole().contains("OWNER")) {
            return ResponseEntity.ok(userService.getAll());
        }
        return ResponseEntity.ok(userService.getUserByVkToken(userFromSession.getId()));
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @PostMapping(value = "/get/client-data")
    public ResponseEntity<List<ClientData>> getVkTokenSender(@RequestParam("mailId") Long id) {
        return ResponseEntity.ok(mailingMessageSendService.getClientDataById(id));
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @GetMapping(value = "/get/no/send")
    public ResponseEntity<List<MailingMessage>> getNoSendId() {
        return ResponseEntity.ok(mailingMessageSendService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @PostMapping(value = "/get/message/id")
    public ResponseEntity<MailingMessage> getMailingMessageById(@RequestParam("messageId") Long id) {
        return ResponseEntity.ok(mailingMessageSendService.get(id));
    }

}
