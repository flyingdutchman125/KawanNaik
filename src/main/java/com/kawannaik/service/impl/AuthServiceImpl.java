package com.kawannaik.service.impl;

import com.kawannaik.dto.request.LoginRequest;
import com.kawannaik.dto.request.RegisterRequest;
import com.kawannaik.dto.response.AuthResponse;
import com.kawannaik.exception.BadRequestException;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.security.CustomUserDetails;
import com.kawannaik.security.JwtTokenProvider;
import com.kawannaik.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            PenggunaRepository penggunaRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (penggunaRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email sudah terdaftar: " + request.getEmail());
        }

        Pengguna pengguna = Pengguna.builder()
                .nama(request.getNama())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .peran(Peran.PENDAKI)
                .levelFisik(request.getLevelFisik() != null ? request.getLevelFisik() : LevelFisik.PEMULA)
                .pengalaman(request.getPengalaman())
                .kontakDarurat(request.getKontakDarurat())
                .build();

        Pengguna saved = penggunaRepository.save(pengguna);

        CustomUserDetails userDetails = new CustomUserDetails(saved);
        String token = tokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(saved.getId())
                .nama(saved.getNama())
                .email(saved.getEmail())
                .peran(saved.getPeran())
                .levelFisik(saved.getLevelFisik())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Pengguna pengguna = userDetails.getPengguna();
        String token = tokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(pengguna.getId())
                .nama(pengguna.getNama())
                .email(pengguna.getEmail())
                .peran(pengguna.getPeran())
                .levelFisik(pengguna.getLevelFisik())
                .build();
    }
}
