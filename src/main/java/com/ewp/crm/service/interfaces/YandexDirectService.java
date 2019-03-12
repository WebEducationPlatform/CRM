package com.ewp.crm.service.interfaces;

import org.json.JSONException;

import java.io.IOException;

public interface YandexDirectService {

    String getYandexDirectBalance() throws JSONException, IOException;
    String getYandexDirectStats() throws JSONException, IOException;
}