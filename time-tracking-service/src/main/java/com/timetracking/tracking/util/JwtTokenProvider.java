package com.timetracking.tracking.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;

  public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    }
    return false;
  }
}