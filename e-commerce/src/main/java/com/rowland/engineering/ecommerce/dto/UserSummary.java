package com.rowland.engineering.ecommerce.dto;

import com.rowland.engineering.ecommerce.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String email;
}
