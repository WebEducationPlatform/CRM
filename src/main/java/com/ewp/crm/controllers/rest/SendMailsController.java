package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ImageUploadDto;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
import com.ewp.crm.service.interfaces.ListMailingService;
import com.ewp.crm.service.interfaces.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
@PropertySource("file:./ckeditor.properties")
public class SendMailsController {

    private final String emailPattern = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
    private final String vkPattern = "^[^a-zA-Z]*$";
    private final String smsPattern = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    private String pattern;
    private String template;
    private MailingMessageRepository mailingMessageRepository;
    private final MailingService mailingService;
    private final ListMailingService listMailingService;
    private final UserService userService;



    @Autowired
    public SendMailsController(MailingMessageRepository mailingMessageRepository,
                               MailingService mailingService, ListMailingService listMailingService, UserService userService) {
        this.mailingMessageRepository = mailingMessageRepository;
        this.mailingService = mailingService;
        this.listMailingService = listMailingService;
        this.userService = userService;

    }

    @PostMapping(value = "/client/mailing/send")
    public ResponseEntity<String> parseClientData(@RequestParam("type") String type,
                                                  @RequestParam("templateText") String templateText,
                                                  @RequestParam("recipients") String recipients,
                                                  @RequestParam("text") String text,
                                                  @RequestParam("date") String date,
                                                  @RequestParam("sendnow") boolean sendnow,
                                                  @RequestParam("vkType") String vkType,
                                                  @RequestParam("listMailing") String listMailingName,
                                                  @AuthenticationPrincipal User userFromSession) {
        MailingMessage message;
        User user = userService.getUserByEmail(userFromSession.getEmail());
        if(!listMailingName.equals("null")) {
            ListMailing listMailing = listMailingService.getByListName(listMailingName);
            switch (type) {
                case "vk":
                    recipients = listMailing.getRecipientsVk();
                    break;
                case "sms":
                    recipients = listMailing.getRecipientsSms();
                    break;
                case "email":
                    recipients = listMailing.getRecipientsEmail();
                    break;
            }
        }
        switch (type) {
            case "vk":
                pattern = vkPattern;
                template = text;
                recipients = recipients.replaceAll("\\p{Punct}|", "");
                break;
            case "sms":
                pattern = smsPattern;
                template = text;
                break;
            case "email":
                pattern = emailPattern;
                template = templateText;
                break;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК");
        LocalDateTime destinationDate = LocalDateTime.parse(date, dateTimeFormatter);
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        Set<ClientData> clientsInfo = new HashSet<>();
        if (type.equals("vk")) {
            String[] vkIds = recipients.split("\n");
            Arrays.asList(vkIds).forEach(x -> {
                Matcher vkMatcher = p.matcher(x);
                if (vkMatcher.find() && !x.contains(" ") && !x.equals("")) {
                    clientsInfo.add(new ClientData(x));
                }
            });
        } else {
            Matcher matcher2 = p.matcher(recipients);
            while (matcher2.find()) {
                clientsInfo.add(new ClientData(matcher2.group()));
            }
        }
        if (type.equals("vk")) {
            message = new MailingMessage(type, template, clientsInfo, destinationDate, vkType, user.getId());
        }
        else {
            message = new MailingMessage(type, template, clientsInfo, destinationDate, user.getId());
        }
        if (sendnow) {
            if (!mailingService.sendMessage(message)) {
                return ResponseEntity.noContent().build();
            }
        } else {
            mailingService.addMailingMessage(message);
        }
        return ResponseEntity.ok("");
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

    @GetMapping(value = "/mailing/history", produces = "application/json")
    public ResponseEntity<List<MailingMessage>> getHistoryMail(@AuthenticationPrincipal User userFromSession) {
        List<MailingMessage> list;
        String role = "OWNER";
        for (Role i : userFromSession.getRole()) {
            if(i.getRoleName().equalsIgnoreCase("ADMIN")) {
                role = i.getRoleName();
            }
        }
        if(role.equals("ADMIN")) {
            list = mailingMessageRepository.findAll();
        } else {
            list = mailingMessageRepository.getUserMail(userFromSession.getId());
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/mailing/manager/history")
    public ResponseEntity<List<MailingMessage>> getHistoryMailForManager(@RequestParam("managerEmail") String email) {
        List<MailingMessage> list;
        if(!email.equalsIgnoreCase("null")) {
            User user = userService.getUserByEmail(email);
            list = mailingMessageRepository.getUserMail(user.getId());
        } else {
            list = mailingMessageRepository.findAll();
        }
        return ResponseEntity.ok(list);

    }
}