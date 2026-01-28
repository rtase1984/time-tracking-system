package com.timetracking.auth.service;

import com.timetracking.auth.domain.dto.AuthResponse;
import com.timetracking.auth.domain.dto.ChangePasswordRequest;
import com.timetracking.auth.domain.dto.LoginRequest;
import com.timetracking.auth.domain.dto.RefreshTokenRequest;
import com.timetracking.auth.domain.dto.RegisterRequest;
import com.timetracking.auth.domain.dto.UpdateUserRequest;
import com.timetracking.auth.domain.dto.UserResponse;
import java.util.UUID;

public interface AuthService {
  AuthResponse login(LoginRequest request);
  AuthResponse register(RegisterRequest request);
  AuthResponse refreshToken(RefreshTokenRequest request);
  void logout(String token);
  UserResponse getCurrentUser();
  UserResponse getUserById(UUID userId);
  UserResponse updateUser(UUID userId, UpdateUserRequest request);
  void changePassword(ChangePasswordRequest request);
}
