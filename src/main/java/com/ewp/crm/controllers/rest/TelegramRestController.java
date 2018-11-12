package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.interfaces.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping("/rest/telegram")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class TelegramRestController {

    private TelegramService telegramService;

    @Autowired
    public TelegramRestController(TelegramService telegramService) {
        this.telegramService = telegramService;
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

    @GetMapping("/messages/chat")
    public HttpStatus getChatMessages(@RequestParam("chatId") Long chatId) {
        telegramService.getChatMessages(chatId);
        return HttpStatus.OK;
    }

    @GetMapping("/logout")
    public HttpStatus logoutFromTelegram() {
        telegramService.logout();
        return HttpStatus.OK;
    }
}
