package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class SendMailsController {

    private final String emailPattern = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
    private final String vkPattern = "^[^a-zA-Z]*$";
    private final String smsPattern = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    private String pattern;
    private String template;
    private MailingMessageRepository mailingMessageRepository;
    private final MailingService mailingService;

    @Autowired
    public SendMailsController(MailingMessageRepository mailingMessageRepository,
                               MailingService mailingService) {
        this.mailingMessageRepository = mailingMessageRepository;
        this.mailingService = mailingService;
    }

    @PostMapping(value = "/client/mailing/send")
    public ResponseEntity<String> parseClientData(@RequestParam("type") String type,
                                                  @RequestParam("templateText") String templateText,
                                                  @RequestParam("recipients") String recipients,
                                                  @RequestParam("text") String text,
                                                  @RequestParam("date") String date,
                                                  @RequestParam("sendnow") boolean sendnow) {

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

        MailingMessage message = new MailingMessage(type, template, clientsInfo, destinationDate);

        if (sendnow) {
            mailingService.sendMessage(message);
        } else {
            mailingService.addMailingMessage(message);
        }

        return ResponseEntity.ok("");
    }
}