package com.ewp.crm.exceptions.parse;

import java.text.ParseException;

public class ParseClientException extends Exception {

    public ParseClientException(String message) {
        super(message);
    }

    public ParseClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
