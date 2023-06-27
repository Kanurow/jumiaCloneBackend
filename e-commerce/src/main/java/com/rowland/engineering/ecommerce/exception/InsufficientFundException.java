package com.rowland.engineering.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundException extends RuntimeException {


    private final double discrepancy;
    public InsufficientFundException( double discrepancy) {
        super(String.format("$ %s is needed to complete this purchase. Failed!.",  discrepancy));
        this.discrepancy = discrepancy;
    }
}
