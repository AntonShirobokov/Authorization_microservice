package com.shirobokov.authorization_microservice.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String email;
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String middleName;
}
