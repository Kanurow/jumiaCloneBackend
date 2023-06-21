package com.rowland.engineering.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReceiverNotFoundException extends RuntimeException {

    public ReceiverNotFoundException(String message) {
        super(message);
    }
}

