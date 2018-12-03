package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.TelegramService;
import org.drinkless.tdlib.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/telegram")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class TelegramRestController {

    private final TelegramService telegramService;
    private final ClientService clientService;
    private final SocialProfileService socialProfileService;
    private static final int MESSAGE_LIMIT = 40;

    @Autowired
    public TelegramRestController(TelegramService telegramService, ClientService clientService,
                                  SocialProfileService socialProfileService) {
        this.telegramService = telegramService;
        this.clientService = clientService;
        this.socialProfileService = socialProfileService;
    }

    @GetMapping("/phone-code")
    public HttpStatus sendAuthPhone(@RequestParam("phone") String phone) {
        telegramService.sendAuthPhone(phone);
        return HttpStatus.OK;
    }

    @GetMapping("/sms-code")
    public HttpStatus sendAuthCodeFromSms(@RequestParam("code") String code) {
        telegramService.sentAuthCode(code);
        return HttpStatus.OK;
    }

    @GetMapping("/messages/chat/open")
    public ResponseEntity<TdApi.Messages> getChatMessages(@RequestParam("clientId") Long clientId) {
        List<SocialProfile> profiles =  clientService.getClientByID(clientId).getSocialProfiles();
        ResponseEntity<TdApi.Messages> result = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        for (SocialProfile profile : profiles) {
            if("telegram".equals(profile.getSocialProfileType().getName())) {
                String chatId = profile.getLink();
                TdApi.Messages messages = telegramService.getChatMessages(Long.parseLong(chatId), MESSAGE_LIMIT);
                result = new ResponseEntity<>(messages, HttpStatus.OK);
                break;
            }
        }
        return result;
    }

    @GetMapping("/messages/chat/close")
    public HttpStatus closeChat(@RequestParam("clientId") Long clientId) {
        List<SocialProfile> profiles =  clientService.getClientByID(clientId).getSocialProfiles();
        HttpStatus result = HttpStatus.NOT_FOUND;
        for (SocialProfile profile : profiles) {
            if("telegram".equals(profile.getSocialProfileType().getName())) {
                String chatId = profile.getLink();
                telegramService.closeChat(Long.parseLong(chatId));
                result = HttpStatus.OK;
                break;
            }
        }
        return result;
    }

    @GetMapping("/messages/chat/unread")
    public ResponseEntity<TdApi.Messages> getUnreadChatMessages(@RequestParam("clientId") Long clientId, @RequestParam("lastMessageId") Long lastMessageId) {
        List<SocialProfile> profiles =  clientService.getClientByID(clientId).getSocialProfiles();
        ResponseEntity<TdApi.Messages> result = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        for (SocialProfile profile : profiles) {
            if("telegram".equals(profile.getSocialProfileType().getName())) {
                String chatId = profile.getLink();
                TdApi.Messages messages = telegramService.getUnreadMessagesFromChat(Long.parseLong(chatId), lastMessageId, MESSAGE_LIMIT);
                result = new ResponseEntity<>(messages, HttpStatus.OK);
                break;
            }
        }
        return result;
    }

    @PostMapping("/message/send")
    public HttpStatus getChatMessages(@RequestParam("clientId") long clientId, @RequestParam("text") String text) {
        HttpStatus result = HttpStatus.NOT_FOUND;
        Optional<SocialProfile> profile = socialProfileService.getSocialProfileByClientIdAndTypeName(clientId, "telegram");
        if (profile.isPresent()) {
            telegramService.sendChatMessage(Long.parseLong(profile.get().getLink()), text);
            result = HttpStatus.OK;
        }
        return result;
    }

    @GetMapping("/me")
    public ResponseEntity<TdApi.User> getCurrentUser() {
        return new ResponseEntity<>(telegramService.getMe(), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<TdApi.User> getUserById(@RequestParam("id") int userId) {
        return new ResponseEntity<>(telegramService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("/user/photos")
    public ResponseEntity<TdApi.UserProfilePhotos> getProfilePhotosByUserId(@RequestParam("id") int userId) {
        return new ResponseEntity<>(telegramService.getUserPhotos(userId), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public HttpStatus logoutFromTelegram() {
        telegramService.logout();
        return HttpStatus.OK;
    }
}
