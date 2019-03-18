package com.ewp.crm.service.interfaces;

import org.json.JSONException;

import java.io.IOException;

public interface AdReportService {

    String getBalance() throws JSONException, IOException;

    String getSpentMoney() throws JSONException, IOException;

}