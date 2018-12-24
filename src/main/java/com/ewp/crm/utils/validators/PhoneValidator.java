package com.ewp.crm.utils.validators;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PhoneValidator {

    public boolean phoneValidation(String phone) {
        Pattern pattern = Pattern.compile("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public String phoneRestore(String phone) {
        if (phone != null && phoneValidation(phone)) {
            if (phone.startsWith("8")) {
                phone = phone.replaceFirst("8", "7");
            }
            phone = phone.replaceAll("[- _():=+]", "")
                    .replaceAll("\\s", "");
        } else {
            phone = "";
        }
        return phone;
    }
}
