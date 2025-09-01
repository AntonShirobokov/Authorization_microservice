package com.shirobokov.authorization_microservice.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtKeyProperties {

    @Value("${jwt.private.key.path}")
    private String privateKeyPath;

    @Value("${jwt.public.key.path}")
    private String publicKeyPath;

}
