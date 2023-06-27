package com.rowland.engineering.ecommerce.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String username;
    private String mobile;
    private String email;
}
