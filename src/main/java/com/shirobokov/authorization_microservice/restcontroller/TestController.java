package com.shirobokov.authorization_microservice.restcontroller;


import com.shirobokov.authorization_microservice.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/testswagger")
@Tag(name="Тестовый контроллер", description = "Тестовый контроллер для проверки работы springdoc")
public class TestController {

    @Operation(summary = "Тестовый метод", description = "Метод POST")
    @PostMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World");
    }


    @GetMapping("/user")
    public ResponseEntity<String> getUser() {
        User user = new User(null, "jfdfd", "gkdgkd");
        return ResponseEntity.ok(user.getEmail());
    }
}
