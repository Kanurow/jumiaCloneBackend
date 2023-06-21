package com.rowland.engineering.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String username;

    private String mobile;
    private String email;
    private Double accountBalance;
    private String password;
}
