package com.shirobokov.authorization_microservice.restcontroller;


import com.shirobokov.authorization_microservice.dto.RefreshTokenDTO;
import com.shirobokov.authorization_microservice.dto.UserLoginDTO;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.security.CustomUserDetails;
import com.shirobokov.authorization_microservice.service.JwtTokenService;
import com.shirobokov.authorization_microservice.service.UserService;
import com.shirobokov.authorization_microservice.util.JwtKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthorizationController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) throws Exception {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPasswordHash()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userService.findUser(userDetails.getUsername());

        Map<Object,Object> keys = jwtTokenService.generateAccessAndRefreshTokens(user);

        return ResponseEntity.status(HttpStatus.OK).body(keys);
    }

    @GetMapping("/keys")
    public ResponseEntity<?> keys () {
        Map<Object,Object> keys = new HashMap<Object,Object>();
        keys.put("privateKey", jwtTokenService.getPrivateKey().getEncoded());

        keys.put("publicKey", jwtTokenService.getPublicKey().getEncoded());

        return ResponseEntity.ok().body(keys);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> getNewAccessToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {

        boolean isValid = jwtTokenService.validateRefreshToken(refreshTokenDTO.getRefreshToken());

        return ResponseEntity.ok().body(isValid);
    }

}
