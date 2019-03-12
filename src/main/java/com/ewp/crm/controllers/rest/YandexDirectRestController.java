package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.impl.YandexDirectServiceImpl;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/balance/yandex")
public class YandexDirectRestController {

    private final YandexDirectServiceImpl yandexDirectService;

    @Autowired
    public YandexDirectRestController(YandexDirectServiceImpl yandexDirectService) {
        this.yandexDirectService = yandexDirectService;
    }

    @GetMapping
    public ResponseEntity<String> getBalance() throws IOException, JSONException {
        return new ResponseEntity<>(yandexDirectService.getYandexDirectBalance(), HttpStatus.OK);
    }
}