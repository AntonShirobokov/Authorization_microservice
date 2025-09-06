package com.shirobokov.authorization_microservice.repository;

import com.shirobokov.authorization_microservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenBlacklistRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByJti(UUID token_jti);
}
