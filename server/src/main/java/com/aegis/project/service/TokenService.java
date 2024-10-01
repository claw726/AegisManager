package com.aegis.project.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TokenService {
    private final String key = "superSecretAegisKey";
    private final long expirationTime = 1000 * 60 * 15; // 15 minutes
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    public String generateToken(Authentication auth) {
        String token = Jwts.builder()
                .setSubject(auth.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        logger.info("Generated token: {}", token); // Log the token

        return token;
    }
}
