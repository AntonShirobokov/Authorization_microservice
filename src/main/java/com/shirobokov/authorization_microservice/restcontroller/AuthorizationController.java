package com.shirobokov.authorization_microservice.restcontroller;


import com.shirobokov.authorization_microservice.dto.RefreshTokenDTO;
import com.shirobokov.authorization_microservice.dto.UserLoginDTO;
import com.shirobokov.authorization_microservice.entity.RefreshToken;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.security.CustomUserDetails;
import com.shirobokov.authorization_microservice.service.JwtTokenService;
import com.shirobokov.authorization_microservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController {



    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) throws Exception {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userService.findUserByEmail(userDetails.getUsername());

        Map<Object,Object> keys = jwtTokenService.generateAccessAndRefreshTokens(user);

        return ResponseEntity.status(HttpStatus.OK).body(keys);
    }

    @GetMapping("/keys")
    public ResponseEntity<?> keys () {
        Map<Object,Object> keys = new HashMap<>();

        keys.put("privateKey", jwtTokenService.getPrivateKey().getEncoded());

        keys.put("publicKey", jwtTokenService.getPublicKey().getEncoded());

        return ResponseEntity.ok().body(keys);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> getNewAccessToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {

        boolean isValid = jwtTokenService.validateRefreshToken(refreshTokenDTO.getRefreshToken());

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "invalid_refresh_token",
                    "message", "Refresh token is expired or invalid"));
        }

        try {
            Map<Object, Object> accessToken = jwtTokenService.updateAccessToken(refreshTokenDTO.getRefreshToken());
            return ResponseEntity.ok(accessToken);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "server_error",
                    "message", "Не удалось обновить access token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenDTO refreshTokenDTO) {

        boolean isValid = jwtTokenService.validateRefreshToken(refreshTokenDTO.getRefreshToken());

        if (isValid) {
            try {
                jwtTokenService.addRefreshTokenToBlacklist(refreshTokenDTO.getRefreshToken());
            } catch (Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Ошибка при добавлении токена в blacklist"));
            }

            return ResponseEntity.ok(Map.of("message", "Refresh token добавлен в blacklist"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Refresh token is expired or invalid"));
    }
}
