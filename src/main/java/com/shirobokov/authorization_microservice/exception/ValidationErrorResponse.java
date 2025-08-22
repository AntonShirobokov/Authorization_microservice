package com.shirobokov.authorization_microservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;


@AllArgsConstructor
@Getter
@Setter
public class ValidationErrorResponse {
    private int status;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
}
