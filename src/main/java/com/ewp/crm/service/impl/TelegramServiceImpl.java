package com.ewp.crm.service.impl;

import com.ewp.crm.service.interfaces.TelegramService;
import org.drinkless.tdlib.TelegramClient;
import org.springframework.stereotype.Service;

@Service
public class TelegramServiceImpl implements TelegramService {

    private TelegramClient client;

    public TelegramServiceImpl() {
//        System.loadLibrary("tdjni");
        this.client = new TelegramClient();
    }

    @Override
    public void sendAuthPhone(String phone) {
        client.receiveQueries(300);
        System.out.println(client.getEventIds());
        System.out.println(client.getEvents());
    }

}
