package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.Message;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SendMailsController {
    private final String emailPattern = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
    private final String vkPattern = "^[^a-zA-Z]*$";
    private final String smsPattern = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    private String pattern;
    private String template;
    private String[] textData;
    private final MailingMessageRepository mailingMessageRepository;
    private final MailingService mailingService;

    @Autowired
    public SendMailsController(MailingMessageRepository mailingMessageRepository, MailingService mailingService) {
        this.mailingMessageRepository = mailingMessageRepository;
        this.mailingService = mailingService;
    }


    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    @RequestMapping(value = "/client/mailing/send", method = RequestMethod.POST)
    public ResponseEntity<String> parseClientData(@RequestParam("type") String type, @RequestParam("templateText") String templateText,
                                                  @RequestParam("text") String text, @RequestParam("date") String date, @RequestParam("sendnow") boolean sendnow) {

        switch (type) {
            case "vk":
                pattern = vkPattern;
                textData = text.split("clientData", 2);
                template = textData[0];
                textData[1] = textData[1].replaceAll("\\p{Punct}|", "");
                break;
            case "sms":
                pattern = smsPattern;
                textData = text.split("clientData", 2);
                template = textData[0];
                break;
            case "email":
                pattern = emailPattern;
                textData = templateText.split("clientData", 2);
                template = textData[0];
                break;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.YYYY HH:mm МСК");
        LocalDateTime destinationDate = LocalDateTime.parse(date, dateTimeFormatter);
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        Set<ClientData> clientsInfo = new HashSet<>();
        if (type.equals("vk")) {
            String[] vkIds = textData[1].split("\n");
            Arrays.asList(vkIds).forEach(x -> {
                Matcher vkMatcher = p.matcher(x);
                if (vkMatcher.find() && !x.contains(" ") && !x.equals("")) {
                    clientsInfo.add(new ClientData(x));
                }
            });
        } else {
            Matcher matcher2 = p.matcher(textData[1]);
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