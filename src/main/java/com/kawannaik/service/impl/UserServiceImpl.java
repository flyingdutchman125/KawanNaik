package com.kawannaik.service.impl;

import com.kawannaik.dto.request.UpdateProfileRequest;
import com.kawannaik.dto.response.UserProfileResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Pengguna;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PenggunaRepository penggunaRepository, PasswordEncoder passwordEncoder) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        Pengguna pengguna = penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));
        return mapToResponse(pengguna);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        Pengguna pengguna = penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));

        if (StringUtils.hasText(request.getNama())) {
            pengguna.setNama(request.getNama());
        }
        if (request.getLevelFisik() != null) {
            pengguna.setLevelFisik(request.getLevelFisik());
        }
        if (request.getPengalaman() != null) {
            pengguna.setPengalaman(request.getPengalaman());
        }
        if (request.getKontakDarurat() != null) {
            pengguna.setKontakDarurat(request.getKontakDarurat());
        }
        if (StringUtils.hasText(request.getPasswordBaru())) {
            pengguna.setPasswordHash(passwordEncoder.encode(request.getPasswordBaru()));
        }

        Pengguna updated = penggunaRepository.save(pengguna);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return penggunaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        Pengguna pengguna = penggunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(pengguna);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!penggunaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pengguna dengan ID " + id + " tidak ditemukan");
        }
        penggunaRepository.deleteById(id);
    }

    private UserProfileResponse mapToResponse(Pengguna pengguna) {
        return UserProfileResponse.builder()
                .id(pengguna.getId())
                .nama(pengguna.getNama())
                .email(pengguna.getEmail())
                .peran(pengguna.getPeran())
                .levelFisik(pengguna.getLevelFisik())
                .pengalaman(pengguna.getPengalaman())
                .kontakDarurat(pengguna.getKontakDarurat())
                .createdAt(pengguna.getCreatedAt())
                .build();
    }
}
