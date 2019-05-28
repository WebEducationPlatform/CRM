package com.ewp.crm.util.validators;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PhoneValidator {
/*
    разрешает следующие форматы
    +7(903)888-88-88
    8(999)99-999-99
    8 927 12 12 888
    (495)1234567
    (495) 123 45 67
    +380(67)777-7-777
    001-541-754-3010
    +1-541-754-3010
    19-49-89-636-48018
    +233 205599853
    не допускает:
    8965****25
    и прочие включение знаков символов ()- _:=+
*/

    public boolean phoneValidation(String phone) {
        Pattern pattern = Pattern.compile("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    /*
    восстанавливает номер исключая в нем символы ()- _:=+ и меняя в федеральных номерах РФ первую цифру 8 на 7
*/
    public String phoneRestore(String phone) {
        if (phone != null && phoneValidation(phone)) {
            if (phone.startsWith("8")) {
                phone = phone.replaceFirst("8", "7");
            }
            phone = phone.replaceAll("[- _():=+]", "")
                    .replaceAll("\\s", "");
        }
        
        return phone;
    }
}