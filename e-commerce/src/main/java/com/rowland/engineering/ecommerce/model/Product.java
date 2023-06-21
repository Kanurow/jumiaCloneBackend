package com.rowland.engineering.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rowland.engineering.ecommerce.dto.ProductRequest;
import jakarta.persistence.*;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Size(max = 40)
    @Column(name = "product_name")
    private String productName;

    @NotBlank
    @Size(max = 40)
    @Column(name = "category")
    private String category;
    

    @NotNull
    @Positive
    @Column(name = "selling_price")
    private Double sellingPrice;

    @NotNull
    @Column(name = "amount_discounted")
    private Double amountDiscounted;


    @NotNull
    @Column(name = "percentage_discount")
    private Integer percentageDiscount;

    @NotNull
    @Positive
    private Integer quantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Lob
    @Column(name = "image")
    private byte[] image;




}



