package com.kawannaik.controller;

import com.kawannaik.dto.request.UpdateProfileRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.UserProfileResponse;
import com.kawannaik.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Pengguna", description = "Profil akun pendaki yang sedang login")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Profil saya", description = "Mengambil data profil pengguna yang sedang login")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Authentication authentication) {
        UserProfileResponse profile = userService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(profile, "Berhasil mengambil profil pengguna"));
    }

    @Operation(summary = "Perbarui profil", description = "Memperbarui data profil, level fisik, pengalaman, dan kontak darurat")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updated = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profil berhasil diperbarui"));
    }
}
