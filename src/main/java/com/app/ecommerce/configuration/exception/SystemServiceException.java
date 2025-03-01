package com.app.ecommerce.configuration.exception;


import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class SystemServiceException extends RuntimeException {

    private final HttpStatus status;

    public SystemServiceException(ExceptionMessages exceptionMessage) {
        super(exceptionMessage.getMessage());
        this.status = exceptionMessage.getStatus();
    }
}

