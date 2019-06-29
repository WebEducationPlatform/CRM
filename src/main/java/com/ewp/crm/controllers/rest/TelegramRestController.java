package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.service.impl.TelegramServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SocialProfileService;
import com.ewp.crm.service.interfaces.TelegramService;
import org.drinkless.tdlib.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/telegram")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', ' HR')")
public class TelegramRestController {

    private final TelegramService telegramService;
    private final ClientService clientService;
    private final SocialProfileService socialProfileService;
    private final TelegramServiceImpl telegramServiceImpl;
    private static final int MESSAGE_LIMIT = 40;

    private static Logger logger = LoggerFactory.getLogger(TelegramRestController.class);

    @Autowired
    public TelegramRestController(TelegramService telegramService, ClientService clientService,
                                  SocialProfileService socialProfileService, TelegramServiceImpl telegramServiceImpl) {
        this.telegramService = telegramService;
        this.clientService = clientService;
        this.telegramServiceImpl = telegramServiceImpl;
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
    public ResponseEntity<Map<String, Object>> getChatMessages(@RequestParam("clientId") Long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent()) {
            List<SocialProfile> profiles = client.get().getSocialProfiles();
            TdApi.Messages messages;
            Optional<TdApi.Chat> chat;
            ResponseEntity result = new ResponseEntity(new HashMap<String, Object>(), HttpStatus.OK);
            for (SocialProfile profile : profiles) {
                if ("telegram".equals(profile.getSocialNetworkType().getName())) {
                    String chatId = profile.getSocialId();
                    messages = telegramService.getChatMessages(Long.parseLong(chatId), MESSAGE_LIMIT);
                    Map<String, Object> map = new HashMap<>();
                    map.put("messages", messages);
                    chat = telegramService.getChat(Long.parseLong(chatId));
                    if (chat.isPresent()) {
                        map.put("chat", chat.get());
                    }
                    result = new ResponseEntity<>(map, HttpStatus.OK);
                    break;
                }
            }
            return result;
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/messages/chat/close")
    public HttpStatus closeChat(@RequestParam("clientId") Long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent()) {
            List<SocialProfile> profiles = client.get().getSocialProfiles();
            HttpStatus result = HttpStatus.NOT_FOUND;
            for (SocialProfile profile : profiles) {
                if ("telegram".equals(profile.getSocialNetworkType().getName())) {
                    String chatId = profile.getSocialId();
                    telegramService.closeChat(Long.parseLong(chatId));
                    result = HttpStatus.OK;
                    break;
                }
            }
            return result;
        }
        return HttpStatus.NOT_FOUND;
    }

    @GetMapping("/messages/chat/unread")
    public ResponseEntity<Map<String, Object>> getUnreadChatMessages(@RequestParam("clientId") Long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent()) {
            List<SocialProfile> profiles = client.get().getSocialProfiles();
            TdApi.Messages messages = new TdApi.Messages();
            TdApi.Chat chat = new TdApi.Chat();
            ResponseEntity result = ResponseEntity.badRequest().build();
            for (SocialProfile profile : profiles) {
                if ("telegram".equals(profile.getSocialNetworkType().getName())) {
                    String chatId = profile.getSocialId();
                    messages = telegramService.getUnreadMessagesFromChat(Long.parseLong(chatId), MESSAGE_LIMIT);
                    chat = telegramService.getChat(Long.parseLong(chatId)).get();
                    Map<String, Object> map = new HashMap<>();
                    map.put("messages", messages);
                    map.put("chat", chat);
                    result = new ResponseEntity<>(map, HttpStatus.OK);
                    break;
                }
            }
            return result;
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/message/send")
    public ResponseEntity<TdApi.Message> getChatMessages(@RequestParam("clientId") long clientId, @RequestParam("text") String text) {
        Optional<SocialProfile> profile = socialProfileService.getSocialProfileByClientIdAndTypeName(clientId, "telegram");
        ResponseEntity result = ResponseEntity.notFound().build();
        if (profile.isPresent()) {
            result = new ResponseEntity(telegramService.sendChatMessage(Long.parseLong(profile.get().getSocialId()), text), HttpStatus.OK);
        } else {
            Optional<Client> client = clientService.getClientByID(clientId);
            if (client.isPresent() && client.get().getEmail().isPresent() && client.get().getPhoneNumber().isPresent()) {
                int telegramId = telegramService.getClientIdByPhone(client.get().getPhoneNumber().get());
                client.get().getSocialProfiles().add(new SocialProfile(String.valueOf(telegramId), SocialNetworkType.TELEGRAM));
                clientService.update(client.get());
                result = new ResponseEntity(telegramService.sendChatMessage((long) telegramId, text), HttpStatus.OK);
            }
        }
        return result;
    }

    @GetMapping("/me")
    public ResponseEntity<TdApi.User> getCurrentUser() {
        return new ResponseEntity<>(telegramService.getTgMe(), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<TdApi.User> getUserById(@RequestParam("id") long clientId) {
        ResponseEntity result = ResponseEntity.notFound().build();
        Optional<SocialProfile> profile = socialProfileService.getSocialProfileByClientIdAndTypeName(clientId, "telegram");
        if (profile.isPresent()) {
            result = new ResponseEntity(telegramService.getUserById(Integer.parseInt(profile.get().getSocialId())), HttpStatus.OK);
        }
        return result;
    }

    @GetMapping("/id-by-phone")
    public ResponseEntity<Integer> getClientIdByPhone(@RequestParam("phone") String phone) {
        int telegramId = telegramService.getClientIdByPhone(phone);
        Optional<Client> client = clientService.getClientByPhoneNumber(phone);
        if (client.isPresent()) {
            Optional<SocialProfile> profile = socialProfileService.getSocialProfileByClientIdAndTypeName(client.get().getId(), "telegram");
            if (!profile.isPresent() && telegramId != 0) {
                if (client.get().getEmail().isPresent()) {
                    client.get().getSocialProfiles().add(new SocialProfile(String.valueOf(telegramId), SocialNetworkType.TELEGRAM));
                    clientService.update(client.get());
                }
            }
        }
        return new ResponseEntity<>(telegramService.getClientIdByPhone(phone), HttpStatus.OK);
    }

    @GetMapping("/file/photo")
    public ResponseEntity<String> getPhotoByFileId(@RequestParam("id") int fileId) {
        TdApi.File file = telegramService.getFileById(fileId);
        String data = "";
        ResponseEntity<String> result = new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
        try {
            data = telegramService.downloadFile(file);
            result = new ResponseEntity<>(data, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Failed to download file {}", file, e);
        }
        return result;
    }

    @GetMapping("/logout")
    public HttpStatus logoutFromTelegram() {
        telegramService.logout();
        return HttpStatus.OK;
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<Client, Integer>> getAllCliensUnreadCount() {
        return new ResponseEntity<>(telegramServiceImpl.getCountOfNewMessages(), HttpStatus.OK);
    }
}
