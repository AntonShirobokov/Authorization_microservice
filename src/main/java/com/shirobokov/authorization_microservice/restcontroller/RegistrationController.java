package com.shirobokov.authorization_microservice.restcontroller;


import com.shirobokov.authorization_microservice.dto.UserRegistrationDTO;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.exception.UserAlreadyExistsException;
import com.shirobokov.authorization_microservice.exception.ValidationErrorResponse;
import com.shirobokov.authorization_microservice.mapper.UserMapper;
import com.shirobokov.authorization_microservice.service.UserService;
import com.shirobokov.authorization_microservice.util.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {


    private final UserService userService;

    private final UserValidator userValidator;

    private final UserMapper userMapper;


    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) throws Exception {
        try {
            User user = userMapper.toUser(userRegistrationDTO);
            userValidator.validate(user, bindingResult);
            if (bindingResult.hasErrors()){
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(err ->
                        errors.put(err.getField(), err.getDefaultMessage()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), errors, LocalDateTime.now()));
            }
            userService.save(user);
        } catch (UserAlreadyExistsException exception) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже зарегестрирован");
        }

        return ResponseEntity.ok().body("Пользователь зарегестрирован");
    }
}
