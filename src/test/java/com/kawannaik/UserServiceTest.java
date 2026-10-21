package com.kawannaik;

import com.kawannaik.dto.request.UpdateProfileRequest;
import com.kawannaik.dto.response.UserProfileResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PenggunaRepository penggunaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Pengguna pengguna;

    @BeforeEach
    void setUp() {
        pengguna = Pengguna.builder()
                .id(1L)
                .nama("Budi Santoso")
                .email("budi@example.com")
                .passwordHash("hashed_password")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.PEMULA)
                .pengalaman("1 tahun")
                .kontakDarurat("08123456789")
                .build();
    }

    @Test
    void getProfile_Success() {
        when(penggunaRepository.findByEmail("budi@example.com")).thenReturn(Optional.of(pengguna));

        UserProfileResponse response = userService.getProfile("budi@example.com");

        assertNotNull(response);
        assertEquals("Budi Santoso", response.getNama());
        assertEquals("budi@example.com", response.getEmail());
    }

    @Test
    void getProfile_NotFound_ThrowsException() {
        when(penggunaRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile("notfound@example.com"));
    }

    @Test
    void updateProfile_Success_WithNewPassword() {
        when(penggunaRepository.findByEmail("budi@example.com")).thenReturn(Optional.of(pengguna));
        when(passwordEncoder.encode(anyString())).thenReturn("new_hashed_password");
        when(penggunaRepository.save(any(Pengguna.class))).thenReturn(pengguna);

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nama("Budi Updated")
                .levelFisik(LevelFisik.MENENGAH)
                .pengalaman("2 tahun")
                .kontakDarurat("0899999999")
                .passwordBaru("secret123")
                .build();

        UserProfileResponse response = userService.updateProfile("budi@example.com", request);

        assertNotNull(response);
        verify(passwordEncoder, times(1)).encode("secret123");
        verify(penggunaRepository, times(1)).save(pengguna);
    }

    @Test
    void updateProfile_Success_WithoutNewPassword() {
        when(penggunaRepository.findByEmail("budi@example.com")).thenReturn(Optional.of(pengguna));
        when(penggunaRepository.save(any(Pengguna.class))).thenReturn(pengguna);

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nama("Budi Updated")
                .build();

        UserProfileResponse response = userService.updateProfile("budi@example.com", request);

        assertNotNull(response);
        verify(passwordEncoder, never()).encode(anyString());
        verify(penggunaRepository, times(1)).save(pengguna);
    }

    @Test
    void updateProfile_NotFound_ThrowsException() {
        when(penggunaRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UpdateProfileRequest request = UpdateProfileRequest.builder().nama("New Name").build();

        assertThrows(ResourceNotFoundException.class, () -> userService.updateProfile("unknown@example.com", request));
    }

    @Test
    void getAllUsers_Success() {
        when(penggunaRepository.findAll()).thenReturn(Collections.singletonList(pengguna));

        List<UserProfileResponse> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("budi@example.com", users.get(0).getEmail());
    }

    @Test
    void getUserById_Success() {
        when(penggunaRepository.findById(1L)).thenReturn(Optional.of(pengguna));

        UserProfileResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals("Budi Santoso", response.getNama());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(penggunaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void deleteUser_Success() {
        when(penggunaRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(penggunaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(penggunaRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999L));
        verify(penggunaRepository, never()).deleteById(anyLong());
    }
}
