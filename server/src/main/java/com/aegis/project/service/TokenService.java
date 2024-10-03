package com.aegis.project.service;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
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

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }


    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parser().setSigningKey(key).build();
            parser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token is expired: {}", e.getMessage());
        } catch (Exception e) {
            logger.warn("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    public String getUsername(String token) {
        JwtParser parser = Jwts.parser().setSigningKey(key).build();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
