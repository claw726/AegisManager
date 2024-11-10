package com.aegis.project.service;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final SecretKey key = Jwts.SIG.HS512.key().build();
  private final long expirationTime = 1000 * 60 * 60; // 15 minutes
  private static final Logger logger = LoggerFactory.getLogger(
    TokenService.class
  );

  public String generateToken(Authentication auth) {
    String token = Jwts.builder()
      .subject(auth.getName())
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + expirationTime))
      .signWith(key)
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
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      logger.warn("Token is expired: {}", e.getMessage());
    } catch (Exception e) {
      logger.warn("Invalid token: {}", e.getMessage());
    }
    return false;
  }

  public String getUsername(String token) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload()
      .getSubject();
  }
}