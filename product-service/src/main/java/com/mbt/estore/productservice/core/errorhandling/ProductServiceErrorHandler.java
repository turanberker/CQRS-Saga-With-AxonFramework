package com.mbt.estore.productservice.core.errorhandling;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ProductServiceErrorHandler {

    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<ErrorMessage> handleIllegalStateExceptions(IllegalStateException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorMessage(new Date(), ex.getMessage())
                , new HttpHeaders()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessage> handleOtherExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorMessage(new Date(), ex.getMessage())
                , new HttpHeaders()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {CommandExecutionException.class})
    public ResponseEntity<ErrorMessage> handleCommandExecutionExceptions(CommandExecutionException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorMessage(new Date(), ex.getMessage())
                , new HttpHeaders()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
