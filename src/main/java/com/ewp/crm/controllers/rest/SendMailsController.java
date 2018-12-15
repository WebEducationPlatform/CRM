package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientData;
import com.ewp.crm.models.MailingMessage;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ImageUploadDto;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
import com.ewp.crm.service.interfaces.MailingMessageService;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.UserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
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
import java.time.LocalDate;
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
    private final String vkPattern = "[^\\/]+$";// подстрока от последнего "/" до конца строки
    private final String smsPattern = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    private final String allDigitPattern = "\\d+";
    private final String idString = "id";
    private final String zeroString = "0";
    private final String vkURL = "https://vk.com/";
    private String pattern;
    private String template;
    private final MailingMessageService mailingMessageSendService;
    private final MailingService mailingService;
    private final UserService userService;



    @Autowired
    private VKService vkService;

    @Autowired
    public SendMailsController(MailingMessageService mailingMessageSendService, MailingService mailingService,
                               UserService userService) {
        this.mailingMessageSendService = mailingMessageSendService;
        this.mailingService = mailingService;
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
                                                  @AuthenticationPrincipal User userFromSession) {
        MailingMessage message;
        User user = userService.getUserByEmail(userFromSession.getEmail());
        switch (type) {
            case "vk":
                pattern = vkPattern;
                template = text;
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
            Set<String> vkIdSet = new HashSet<>();
            Arrays.asList(vkIds).forEach(x -> {
                Matcher vkMatcher = p.matcher(x);
                if (vkMatcher.find() && !x.contains(" ") && !x.equals(Strings.EMPTY)) {

                    String vkIdentify = vkMatcher.group();
                    if (vkIdentify.startsWith(idString) && vkIdentify.replace(idString, Strings.EMPTY).matches(allDigitPattern)) {
                        vkIdentify = vkIdentify.replace(idString, Strings.EMPTY);
                    }

                    if (!vkIdentify.matches(allDigitPattern)) {
                        vkIdentify = Long.toString(vkService.getVKIdByUrl(vkURL + vkIdentify).orElse(0L));
                    }

                    if (!zeroString.equals(vkIdentify)) {
                        vkIdSet.add(vkIdentify);
                    }
                }
            });

            vkIdSet.forEach(vkIdentify -> clientsInfo.add(new ClientData(vkIdentify)));

        } else {
            Matcher matcher2 = p.matcher(recipients);
            while (matcher2.find()) {
                clientsInfo.add(new ClientData(matcher2.group()));
            }
        }
        if (type.equals("vk")) {
            message = new MailingMessage(type, template, clientsInfo, destinationDate, vkType, user.getId());
        } else {
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

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @GetMapping(value = "/mailing/history", produces = "application/json")
    public ResponseEntity<List<MailingMessage>> getHistoryMail(@AuthenticationPrincipal User userFromSession) {
        if (userFromSession.getRole().contains("OWNER")) {
            return ResponseEntity.ok(mailingMessageSendService.getAll());
        }
        return ResponseEntity.ok(mailingMessageSendService.getUserMail(userFromSession.getId()));
    }

    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    @PostMapping("/mailing/manager/history")
    public ResponseEntity<List<MailingMessage>> getHistoryMailForManager(@RequestParam("managerId") Long id,
                                                                         @RequestParam("managerFromTime") String timeFrom,
                                                                         @RequestParam("managerToTime") String timeTo) {
        List<MailingMessage> list;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (id != null) {
            LocalDateTime destinationDateFrom = LocalDate.parse(timeFrom, dateTimeFormatter).atStartOfDay();
            User user = userService.get(id);
            LocalDateTime destinationDateTo = LocalDate.parse(timeTo, dateTimeFormatter).atStartOfDay();
            list = mailingMessageSendService.getUserByIdAndDate(user.getId(), destinationDateFrom.getDayOfMonth(), destinationDateTo.getDayOfMonth());
        } else if (id == null) {
            LocalDateTime destinationDate = LocalDate.parse(timeFrom, dateTimeFormatter).atStartOfDay();
            LocalDateTime destinationDateTo = LocalDate.parse(timeTo, dateTimeFormatter).atStartOfDay();
            list = mailingMessageSendService.getUserByDate(destinationDate.getDayOfMonth(), destinationDateTo.getDayOfMonth());
        } else {
            list = mailingMessageSendService.getAll();
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

}
