package com.ewp.crm.utils.tdlib;

import org.telegram.api.auth.TLCheckedPhone;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.auth.TLRequestAuthCheckPhone;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Telegram {
    public static void main(String[] args) {
        CRMAbsApiState absApiState = new CRMAbsApiState();
        AppInfo appInfo = new AppInfo(590445, "desktop", "1.0", "1.0", "RU");
        CRMApiCallback apiCallback = new CRMApiCallback();
        TelegramApi api = new TelegramApi(absApiState, appInfo, apiCallback);

        // Create request
        String phoneNumber = "+79162399001";
//        TLRequestAuthCheckPhone checkPhone = new TLRequestAuthCheckPhone();
//        checkPhone.setPhoneNumber(phoneNumber);

        // Call service synchronously
//        TLCheckedPhone checkedPhone = null;
//        try {
//            checkedPhone = api.doRpcCall(checkPhone);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//        System.out.println(checkedPhone);
        //        boolean invited = checkedPhone.;
//        boolean registered = checkedPhone.isPhoneRegistered();
//        System.out.println(registered);
    }
}
