package com.rowland.engineering.ecommerce.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InsufficientFundException.class)
    @ResponseBody
    public ResponseEntity<String> handleInsufficientFundException(InsufficientFundException ex) {
        LOGGER.error("Insufficient Fund Exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ReceiverNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleReceiverNotFoundException(ReceiverNotFoundException ex) {
        LOGGER.error("Beneficiary Not Found: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}
