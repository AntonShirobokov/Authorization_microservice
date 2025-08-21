package com.shirobokov.authorization_microservice.restcontroller;


import com.shirobokov.authorization_microservice.dto.UserDTO;
import com.shirobokov.authorization_microservice.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody UserDTO userDTO){

        return ResponseEntity.ok().build();
    }
}
