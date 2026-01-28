package com.timetracking.auth.service;

import com.timetracking.auth.domain.dto.AuthResponse;
import com.timetracking.auth.domain.dto.ChangePasswordRequest;
import com.timetracking.auth.domain.dto.LoginRequest;
import com.timetracking.auth.domain.dto.RefreshTokenRequest;
import com.timetracking.auth.domain.dto.RegisterRequest;
import com.timetracking.auth.domain.dto.UpdateUserRequest;
import com.timetracking.auth.domain.dto.UserResponse;
import com.timetracking.auth.domain.entity.User;
import com.timetracking.auth.exception.BusinessException;
import com.timetracking.auth.repository.UserRepository;
import com.timetracking.auth.util.JwtTokenProvider;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider tokenProvider;
  private final AuthenticationManager authenticationManager;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    log.info("Login attempt for user: {}", request.getEmail());

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = tokenProvider.generateToken(authentication);
    String refreshToken = tokenProvider.generateRefreshToken(request.getEmail());

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BusinessException("User not found"));

    log.info("User {} logged in successfully", request.getEmail());

    return AuthResponse.builder()
        .token(token)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(tokenProvider.getJwtExpiration())
        .user(mapToUserResponse(user))
        .build();
  }

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    log.info("Registration attempt for user: {}", request.getEmail());

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException("Email already exists");
    }

    User supervisor = null;
    if (request.getSupervisorId() != null) {
      supervisor = userRepository.findById(request.getSupervisorId())
          .orElseThrow(() -> new BusinessException("Supervisor not found"));
    }

    User user = User.builder()
        .email(request.getEmail())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(request.getRole())
        .supervisor(supervisor)
        .active(true)
        .build();

    user = userRepository.save(user);

    String token = tokenProvider.generateToken(user.getEmail());
    String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

    log.info("User {} registered successfully", request.getEmail());

    return AuthResponse.builder()
        .token(token)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(tokenProvider.getJwtExpiration())
        .user(mapToUserResponse(user))
        .build();
  }

  @Override
  public AuthResponse refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();

    if (!tokenProvider.validateToken(refreshToken)) {
      throw new BusinessException("Invalid refresh token");
    }

    String username = tokenProvider.getUsernameFromToken(refreshToken);
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new BusinessException("User not found"));

    String newToken = tokenProvider.generateToken(username);
    String newRefreshToken = tokenProvider.generateRefreshToken(username);

    return AuthResponse.builder()
        .token(newToken)
        .refreshToken(newRefreshToken)
        .tokenType("Bearer")
        .expiresIn(tokenProvider.getJwtExpiration())
        .user(mapToUserResponse(user))
        .build();
  }

  @Override
  public void logout(String token) {
    String username = tokenProvider.getUsernameFromToken(token);

    // Add token to blacklist in Redis
    long expiration = tokenProvider.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis();
    redisTemplate.opsForValue().set(
        "blacklist:" + token,
        username,
        expiration,
        TimeUnit.MILLISECONDS
    );

    log.info("User {} logged out successfully", username);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException("User not found"));

    return mapToUserResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException("User not found"));

    return mapToUserResponse(user);
  }

  @Override
  @Transactional
  public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException("User not found"));

    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getActive() != null) {
      user.setActive(request.getActive());
    }

    user = userRepository.save(user);
    log.info("User {} updated successfully", userId);

    return mapToUserResponse(user);
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException("User not found"));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
      throw new BusinessException("Current password is incorrect");
    }

    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    log.info("Password changed for user {}", email);
  }

  private UserResponse mapToUserResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .fullName(user.getFullName())
        .role(user.getRole())
        .supervisorId(user.getSupervisor() != null ? user.getSupervisor().getId() : null)
        .supervisorName(user.getSupervisor() != null ? user.getSupervisor().getFullName() : null)
        .active(user.getActive())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
