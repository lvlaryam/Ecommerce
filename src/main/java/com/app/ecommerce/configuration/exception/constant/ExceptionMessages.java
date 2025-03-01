package com.app.ecommerce.configuration.exception.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessages {
    AUTHENTICATION_FAILED("Authentication failed", HttpStatus.UNAUTHORIZED ),
    INVALID_REFRESH_TOKEN("Invalid Refresh token", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    NOT_FOUND("Not found", HttpStatus.NOT_FOUND ),
    ACCESS_DENIED("Access denied", HttpStatus.FORBIDDEN),
    INVALID_CODE("Invalid code", HttpStatus.BAD_REQUEST ),

    ;

    private final String message;
    private final HttpStatus status;

    ExceptionMessages(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
