package com.megacart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Lỗi 409
public class PhoneNumberAlreadyInUseException extends RuntimeException {

    public PhoneNumberAlreadyInUseException(String message) {
        super(message);
    }
}