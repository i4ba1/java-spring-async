package com.dev.funcinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnverifiedAccountException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnverifiedAccountException(String message) {
        super(message);
    }
}
