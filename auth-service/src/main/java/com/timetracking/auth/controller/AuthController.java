package com.timetracking.auth.controller;

import com.timetracking.auth.domain.dto.AuthResponse;
import com.timetracking.auth.domain.dto.ChangePasswordRequest;
import com.timetracking.auth.domain.dto.LoginRequest;
import com.timetracking.auth.domain.dto.RefreshTokenRequest;
import com.timetracking.auth.domain.dto.RegisterRequest;
import com.timetracking.auth.domain.dto.UpdateUserRequest;
import com.timetracking.auth.domain.dto.UserResponse;
import com.timetracking.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Authentication and user management endpoints")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Login", description = "Authenticate user and return JWT token")
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Register", description = "Register a new user")
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    AuthResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Logout", description = "Invalidate current JWT token")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7); // Remove "Bearer " prefix
    authService.logout(token);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get current user", description = "Get authenticated user information")
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser() {
    UserResponse response = authService.getCurrentUser();
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Get user by ID", description = "Get user information by ID")
  @GetMapping("/users/{userId}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
    UserResponse response = authService.getUserById(userId);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Update user", description = "Update user information")
  @PutMapping("/users/{userId}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID userId,
      @Valid @RequestBody UpdateUserRequest request) {
    UserResponse response = authService.updateUser(userId, request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Change password", description = "Change current user password")
  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(request);
    return ResponseEntity.noContent().build();
  }
}