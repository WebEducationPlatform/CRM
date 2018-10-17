package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
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
    private String[] textData; //todo это че?
    private String textData1;
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
                                                  @RequestParam("clientData") String clientData,
                                                  @RequestParam("text") String text,
                                                  @RequestParam("date") String date,
                                                  @RequestParam("sendnow") boolean sendnow) {

        switch (type) {
            case "vk":
                pattern = vkPattern;
                template = text;
                clientData = clientData.replaceAll("\\p{Punct}|", "");
                //textData = text.split("clientData", 2);
                //textData[1] = textData[1].replaceAll("\\p{Punct}|", "");
                break;
            case "sms":
                //textData = text.split("clientData", 2);
                pattern = smsPattern;
                template = text;
                break;
            case "email":
                //textData = templateText.split("clientData", 2);
                pattern = emailPattern;
                template = templateText;
                break;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm МСК");
        LocalDateTime destinationDate = LocalDateTime.parse(date, dateTimeFormatter);
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        Set<ClientData> clientsInfo = new HashSet<>();
        if (type.equals("vk")) {
            String[] vkIds = clientData.split("\n");
            Arrays.asList(vkIds).forEach(x -> {
                Matcher vkMatcher = p.matcher(x);
                if (vkMatcher.find() && !x.contains(" ") && !x.equals("")) {
                    clientsInfo.add(new ClientData(x));
                }
            });
        } else {
            Matcher matcher2 = p.matcher(clientData);
            while (matcher2.find()) {
                clientsInfo.add(new ClientData(matcher2.group()));
            }
        }

        MailingMessage message = new MailingMessage(type, template, clientsInfo, destinationDate);
        MailingMessage executeMessage = mailingService.addMailingMessage(message);

        if (sendnow) {
            mailingService.sendMessage(executeMessage);
        }

        return ResponseEntity.ok("");
    }
}