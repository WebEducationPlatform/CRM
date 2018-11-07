package com.ewp.crm.service.interfaces;

public interface TelegramService {

    void sendAuthPhone(String phone);

    void sentAuthCode(String smsCode);
}
