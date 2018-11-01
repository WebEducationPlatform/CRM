package com.ewp.crm.exceptions;

import com.ewp.crm.exceptions.client.ClientExistsException;
import com.ewp.crm.exceptions.email.MessageTemplateException;
import com.ewp.crm.exceptions.status.StatusExistsException;
import com.ewp.crm.exceptions.user.UserEntityException;
import com.ewp.crm.exceptions.user.UserExistsException;
import com.ewp.crm.exceptions.user.UserPhotoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);

    @ExceptionHandler({ClientExistsException.class, StatusExistsException.class,
            MessageTemplateException.class, UserExistsException.class, UserPhotoException.class, UserEntityException.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("409 Status Code", ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

}
