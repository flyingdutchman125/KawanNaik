package com.kawannaik.service;

import com.kawannaik.dto.request.UpdateProfileRequest;
import com.kawannaik.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse getProfile(String email);
    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse getUserById(Long id);
    void deleteUser(Long id);
}
