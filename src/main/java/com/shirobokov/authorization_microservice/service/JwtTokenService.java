package com.shirobokov.authorization_microservice.service;

import com.shirobokov.authorization_microservice.entity.Role;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.util.JwtKeyProperties;
import com.shirobokov.authorization_microservice.util.KeyUtils;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Getter
public class JwtTokenService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 минут
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 дней

    public JwtTokenService(JwtKeyProperties keyProperties) throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey(keyProperties.getPrivateKeyPath());
        this.publicKey = KeyUtils.loadPublicKey(keyProperties.getPublicKeyPath());
    }


    public Map<Object, Object> generateAccessAndRefreshTokens(User user) {
        Map<Object, Object> tokens = new HashMap<>();

        Instant now = Instant.now();

        // Access токен
        String accessToken = Jwts.builder()
                .setSubject(user.getUserId().toString()) // sub = ID пользователя
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(r -> r.getName()).toList()) // можно добавить список ролей
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ACCESS_TOKEN_EXPIRATION)))
                .signWith(privateKey, SignatureAlgorithm.RS256) // подпись приватным ключом RSA
                .compact();

        // Refresh токен
        String refreshToken = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(REFRESH_TOKEN_EXPIRATION)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }


}
