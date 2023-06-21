package com.rowland.engineering.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "favourite", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "product_id",
                "user_id"
        })
})
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;



    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
