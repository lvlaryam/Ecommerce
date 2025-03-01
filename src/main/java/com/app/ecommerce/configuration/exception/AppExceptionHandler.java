package com.app.ecommerce.configuration.exception;

import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class AppExceptionHandler {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @ExceptionHandler(value = {SystemServiceException.class})
    public ResponseEntity<String> handleRelayServiceException(SystemServiceException systemServiceException) {
        logger.log(Level.WARNING, "SystemServiceException had just happened, {0}", systemServiceException.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException authenticationException) {
        logger.log(Level.WARNING, "Authentication Exception had just happened, {0}", authenticationException.getMessage());
        return new ResponseEntity<>(ExceptionMessages.AUTHENTICATION_FAILED.getMessage(), HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<String> handleException(Exception exception) {
        logger.log(Level.WARNING, "Exception had just happened, {0}", exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
