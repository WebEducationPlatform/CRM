package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface IPService {

    void call(String from, String to, Long callId);

    String getBalanceOfVoximplantAccountInfo();

    Optional<String> getVoximplantLoginForWebCall();

    Optional<String> getVoximplantPasswordForWebCall();

    Optional<String> getVoximplantUserLogin(String fullLogin);

    Optional<String> getVoximplantCodeToSetRecord();
}
