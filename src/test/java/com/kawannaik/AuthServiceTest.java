package com.kawannaik;

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
import com.kawannaik.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PenggunaRepository penggunaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private Pengguna pengguna;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .nama("Budi Santoso")
                .email("budi@example.com")
                .password("password123")
                .levelFisik(LevelFisik.MENENGAH)
                .pengalaman("5 gunung")
                .kontakDarurat("08123456789")
                .build();

        pengguna = Pengguna.builder()
                .id(1L)
                .nama("Budi Santoso")
                .email("budi@example.com")
                .passwordHash("hashedPassword")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MENENGAH)
                .pengalaman("5 gunung")
                .kontakDarurat("08123456789")
                .build();
    }

    @Test
    void register_Success() {
        when(penggunaRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(penggunaRepository.save(any())).thenReturn(pengguna);
        when(tokenProvider.generateToken(any())).thenReturn("mockJwtToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("budi@example.com", response.getEmail());
        assertEquals(Peran.PENDAKI, response.getPeran());
        verify(penggunaRepository, times(1)).save(any());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        when(penggunaRepository.existsByEmail("budi@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(penggunaRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("budi@example.com")
                .password("password123")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(pengguna);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(any())).thenReturn("mockJwtToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("budi@example.com", response.getEmail());
    }
}
