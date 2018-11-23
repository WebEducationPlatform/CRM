package com.ewp.crm.controllers.rest;

import com.ewp.crm.CrmApplication;
import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.dto.ImageUploadDto;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
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



    @Value("${ckeditor.img.upload.path}")
    String uploadPath;
    @Value("${ckediror.img.uri}")
    String uploadUri;
    @Value("${ckeditor.img.upload.target.path}")
    String uploadTargetPath;

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @PostMapping(value = "/image/upload", produces = "application/json")
    public ResponseEntity<ImageUploadDto> upload(@RequestPart MultipartFile upload, HttpServletRequest request) throws IOException {
        String sourceName = upload.getOriginalFilename();
        String sourceExt = FilenameUtils.getExtension(sourceName).toLowerCase();
        File destFile;
        File destTargetFile;
        String destFileName;

        destFileName = String.valueOf(System.currentTimeMillis())+"."+sourceExt;
        destFile = new File(uploadPath+destFileName);
        destTargetFile = new File(uploadTargetPath+destFileName);

        destFile.getParentFile().mkdirs();
        destTargetFile.getParentFile().mkdirs();

        upload.transferTo(destFile);
        upload.transferTo(destTargetFile);

        URI imgUrl = URI.create(request.getScheme()+"://"+request.getServerName()+uploadUri+destFileName);

        ImageUploadDto imageUploadDto = new ImageUploadDto(1, destFileName, imgUrl);

        return new ResponseEntity<>(imageUploadDto, HttpStatus.OK);
    }
}