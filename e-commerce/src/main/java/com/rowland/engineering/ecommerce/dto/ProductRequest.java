package com.rowland.engineering.ecommerce.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


@Value
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    @Size(max = 40)
    String productName;

    @NotNull
    @Positive
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull
    @Positive
    @Min(value = 1)
    private Integer quantity;


    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Transient
    MultipartFile imageFile;

    @Lob
    @Column(name = "image")
    byte[] image;
}
