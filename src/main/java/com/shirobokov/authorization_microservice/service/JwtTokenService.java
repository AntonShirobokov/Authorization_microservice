package com.shirobokov.authorization_microservice.service;

import com.shirobokov.authorization_microservice.entity.Role;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.util.JwtKeyProperties;
import com.shirobokov.authorization_microservice.util.KeyUtils;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@Getter
public class JwtTokenService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 минут
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 3; // 1 день

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
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("middleName", user.getMiddleName())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ACCESS_TOKEN_EXPIRATION)))
                .signWith(privateKey, SignatureAlgorithm.RS256) // подпись приватным ключом RSA
                .compact();

        // Refresh токен
        String refreshToken = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(REFRESH_TOKEN_EXPIRATION)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try{
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(refreshToken);

            Claims claims = claimsJws.getPayload();

            Date expiration = claims.getExpiration();
            if (expiration!=null && expiration.before(Date.from(Instant.now()))){
                return false;
            }
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Refresh token expired: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println("Invalid signature: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
    }
}
