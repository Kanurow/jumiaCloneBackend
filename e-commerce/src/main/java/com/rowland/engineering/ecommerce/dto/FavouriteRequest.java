package com.rowland.engineering.ecommerce.dto;

import com.rowland.engineering.ecommerce.model.Product;
import com.rowland.engineering.ecommerce.model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavouriteRequest {
    private Long productId;
}
