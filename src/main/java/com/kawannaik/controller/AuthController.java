package com.kawannaik.controller;

import com.kawannaik.dto.request.LoginRequest;
import com.kawannaik.dto.request.RegisterRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.AuthResponse;
import com.kawannaik.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1. Autentikasi", description = "Endpoint untuk pendaftaran dan login pendaki")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrasi pengguna baru", description = "Mendaftarkan akun pendaki baru ke dalam sistem")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registrasi berhasil"));
    }

    @Operation(summary = "Login pengguna", description = "Autentikasi dengan email dan password, mengembalikan token JWT")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login berhasil"));
    }
}
