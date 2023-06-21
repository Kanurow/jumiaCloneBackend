package com.rowland.engineering.ecommerce.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Double transferAmount;
    private String emailOrAccountNumber;
}
