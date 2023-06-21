package com.rowland.engineering.ecommerce.dto;

import lombok.Data;

@Data
public class PromoCodeRequest {
    private String code;
    private Double promoAmount;
}
