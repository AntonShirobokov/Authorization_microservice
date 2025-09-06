package com.shirobokov.authorization_microservice.service;

import com.shirobokov.authorization_microservice.entity.RefreshToken;
import com.shirobokov.authorization_microservice.entity.User;
import com.shirobokov.authorization_microservice.repository.RefreshTokenBlacklistRepository;
import com.shirobokov.authorization_microservice.util.JwtKeyProperties;
import com.shirobokov.authorization_microservice.util.KeyUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@Service
@Getter
@Slf4j
public class JwtTokenService {

    private final UserService userService;
    private final RefreshTokenBlacklistRepository refreshTokenBlacklistRepository;


    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 минут
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 3; // 3 дня

    public JwtTokenService(JwtKeyProperties keyProperties, UserService userService, RefreshTokenBlacklistRepository refreshTokenBlacklistRepository) throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey(keyProperties.getPrivateKeyPath());
        this.publicKey = KeyUtils.loadPublicKey(keyProperties.getPublicKeyPath());
        this.userService = userService;
        this.refreshTokenBlacklistRepository = refreshTokenBlacklistRepository;
    }


    public Map<Object, Object> generateAccessAndRefreshTokens(User user) {
        Map<Object, Object> tokens = new HashMap<>();

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try{
            Claims claims = parseRefreshToken(refreshToken);


            if (claims.getExpiration() == null) {
                log.warn("Refresh token has no expiration!");
                return false;
            }
            UUID token_jti = UUID.fromString(claims.getId());
            Optional<RefreshToken> token = refreshTokenBlacklistRepository.findByJti(token_jti);

            return token.isEmpty();
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Invalid signature: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }


    public Map<Object, Object> updateAccessToken(String refreshToken) throws Exception{

        User user = getUserFromRefreshToken(refreshToken);

        String accessToken = generateAccessToken(user);

        return Map.of("accessToken", accessToken);
    }

    public Claims parseRefreshToken(String refreshToken) throws JwtException {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        // Генерация Access токена
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles().stream().map(r -> r.getName()).toList())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("middleName", user.getMiddleName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(ACCESS_TOKEN_EXPIRATION)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();

        // Генерация Refresh токена
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(REFRESH_TOKEN_EXPIRATION)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public void addRefreshTokenToBlacklist(String refreshToken) throws Exception{

        Claims claims = parseRefreshToken(refreshToken);

        User user = getUserFromRefreshToken(refreshToken);

        RefreshToken token = new RefreshToken();
        token.setJti(UUID.fromString(claims.getId()));
        token.setExpiresAt(LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault()));
        token.setUser(user);

        refreshTokenBlacklistRepository.save(token);
    }

    public User getUserFromRefreshToken(String refreshToken) throws Exception{
        Claims claims = parseRefreshToken(refreshToken);

        UUID user_id = UUID.fromString(claims.getSubject());

        return userService.findUserByUserId(user_id);
    }
}
