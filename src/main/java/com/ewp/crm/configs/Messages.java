package com.ewp.crm.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("messaging")
//@PropertySource("classpath:messages.yml")
@PropertySource("classpath:messages.yml")

public class Messages {


 /*   @Value("${messaging.phone.calls.delivery-error}")
    private String deliveryError;

    @Value("${messaging.phone.calls.invalid-mobile-phone}")
    private String invalidMobilePhone;

    @Value("${messaging.phone.calls.incorrect-id}")
    private String incorrectId;

    @Value("${messaging.phone.calls.unknown-error}")
    private String unknownError;




    public String getDeliveryError() {
        return deliveryError;
    }

    public String getInvalidMobilePhone() {
        return invalidMobilePhone;
    }

    public String getIncorrectId() {
        return incorrectId;
    }

    public String getUnknownError() {
        return unknownError;
    }*/
}
