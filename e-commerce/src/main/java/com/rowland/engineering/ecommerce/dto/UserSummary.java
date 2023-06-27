package com.rowland.engineering.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String email;
}
