package com.ewp.crm.service.interfaces;

import org.json.JSONException;

import java.io.IOException;

public interface AdReportService {

    String getYandexDirectBalance() throws JSONException, IOException;

    String getYandexDirectSpentMoney() throws JSONException, IOException;

}