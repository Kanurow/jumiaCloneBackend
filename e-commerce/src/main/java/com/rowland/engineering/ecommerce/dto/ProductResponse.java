package com.rowland.engineering.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

@Data
public class ProductResponse {

    private String productName;

    private Integer price;

    private Integer quantity;

}
