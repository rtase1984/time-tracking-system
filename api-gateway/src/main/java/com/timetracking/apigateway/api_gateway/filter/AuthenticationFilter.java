package com.timetracking.apigateway.api_gateway.filter;

import com.timetracking.apigateway.api_gateway.filter.AuthenticationFilter.Config;
import com.timetracking.apigateway.api_gateway.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<Config> {

  private JwtTokenProvider jwtTokenProvider;

  public AuthenticationFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      // Extract JWT token
      String token = extractToken(request);

      if (token == null) {
        log.warn("No JWT token found in request to: {}", request.getPath());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }

      // Validate token
      if (!jwtTokenProvider.validateToken(token)) {
        log.warn("Invalid JWT token for request to: {}", request.getPath());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }

      // Extract username and add to header
      String username = jwtTokenProvider.getUsernameFromToken(token);

      ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
          .header("X-User-Id", username)
          .build();

      log.debug("Authenticated request for user: {}", username);

      return chain.filter(exchange.mutate().request(modifiedRequest).build());
    };
  }

  private String extractToken(ServerHttpRequest request) {
    String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public static class Config {
    // Configuration properties if needed
  }
}
