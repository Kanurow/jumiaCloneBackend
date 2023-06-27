package com.rowland.engineering.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private int alternativePhoneNumber;
    private String deliveryAddress;
    private String additionalInformation;
    private  String region;
    private String state;

    private double total;
    private int quantity;
    private List<OrderHistoryResponse.CartItem> cart;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItem {
        private Long productId;
        private String productName;
        private double price;
        private String imageUrl;
        private int quantity;
        private double subtotal;

    }
}
