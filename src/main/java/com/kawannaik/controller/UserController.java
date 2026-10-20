package com.kawannaik.controller;

import com.kawannaik.dto.request.UpdateProfileRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.UserProfileResponse;
import com.kawannaik.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Authentication authentication) {
        UserProfileResponse profile = userService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(profile, "Berhasil mengambil profil pengguna"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updated = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profil berhasil diperbarui"));
    }
}
