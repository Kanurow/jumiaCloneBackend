package com.rowland.engineering.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "cart_checkout")
public class CartCheckout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private int phoneNumber;
    private int alternativePhoneNumber;
    private String deliveryAddress;
    private String additionalInformation;
    private  String region;
    private String state;

    private double price;
    private int quantity;
//    private double total;

    private Long userId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_checkout_id")
    private List<CartItem> cart;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Entity
    @Table(name = "cart_item")
    public static class CartItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long productId;
        private String productName;
        private double price;
        private int quantity;
        private String imageUrl;
        private double subtotal;

    }
}
