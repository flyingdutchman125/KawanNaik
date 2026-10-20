package com.kawannaik.service;

import com.kawannaik.dto.request.GunungRequest;
import com.kawannaik.dto.response.GunungResponse;

import java.util.List;

public interface GunungService {
    List<GunungResponse> getAllGunung(String search);
    GunungResponse getGunungById(Long id);
    GunungResponse createGunung(GunungRequest request);
    GunungResponse updateGunung(Long id, GunungRequest request);
    void deleteGunung(Long id);
}
