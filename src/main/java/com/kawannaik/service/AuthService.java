package com.kawannaik.service;

import com.kawannaik.dto.request.LoginRequest;
import com.kawannaik.dto.request.RegisterRequest;
import com.kawannaik.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
