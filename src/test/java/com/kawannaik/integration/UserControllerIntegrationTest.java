package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.UpdateProfileRequest;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PesertaRencanaRepository pesertaRepository;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Pengguna testUser;

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        penggunaRepository.deleteAll();

        testUser = penggunaRepository.save(Pengguna.builder()
                .nama("Pengguna Test")
                .email("testuser@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.PEMULA)
                .pengalaman("1 gunung")
                .kontakDarurat("08123456789")
                .build());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "PENDAKI")
    void getMyProfile_Authenticated_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.data.nama").value("Pengguna Test"));
    }

    @Test
    void getMyProfile_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "PENDAKI")
    void updateMyProfile_Authenticated_ShouldReturn200() throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nama("Pengguna Test Diperbarui")
                .levelFisik(LevelFisik.MENENGAH)
                .pengalaman("5 gunung di Jawa")
                .kontakDarurat("08999999999")
                .build();

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nama").value("Pengguna Test Diperbarui"))
                .andExpect(jsonPath("$.data.levelFisik").value("MENENGAH"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "PENDAKI")
    void getAllUsers_AsPendaki_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_AsAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_AsAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
