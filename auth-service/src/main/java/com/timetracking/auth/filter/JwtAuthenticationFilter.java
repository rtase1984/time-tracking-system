package com.timetracking.auth.filter;

import com.timetracking.auth.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt)) {
        // Check if token is blacklisted
        Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + jwt);
        if (Boolean.TRUE.equals(isBlacklisted)) {
          log.warn("Attempt to use blacklisted token");
          filterChain.doFilter(request, response);
          return;
        }

        if (tokenProvider.validateToken(jwt)) {
          String username = tokenProvider.getUsernameFromToken(jwt);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
              );

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.debug("Set authentication for user: {}", username);
        }
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
