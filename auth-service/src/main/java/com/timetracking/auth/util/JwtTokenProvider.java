package com.timetracking.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final long jwtExpiration;
  private final long refreshExpiration;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration}") long jwtExpiration,
      @Value("${jwt.refresh-expiration}") long refreshExpiration) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.jwtExpiration = jwtExpiration;
    this.refreshExpiration = refreshExpiration;
  }

  public String generateToken(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return generateToken(userDetails.getUsername(), jwtExpiration);
  }

  public String generateToken(String username) {
    return generateToken(username, jwtExpiration);
  }

  public String generateRefreshToken(String username) {
    return generateToken(username, refreshExpiration);
  }

  private String generateToken(String username, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", username);
    claims.put("iat", now);
    claims.put("exp", expiryDate);

    return Jwts.builder()
        .claims(claims)
        .subject(username)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(secretKey)
        .compact();
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
    } catch (SecurityException ex) {
      log.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty");
    }
    return false;
  }

  public Date getExpirationDateFromToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    return claims.getExpiration();
  }

  public long getJwtExpiration() {
    return jwtExpiration;
  }
}
