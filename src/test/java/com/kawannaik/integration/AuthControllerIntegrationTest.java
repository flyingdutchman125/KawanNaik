package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.LoginRequest;
import com.kawannaik.dto.request.RegisterRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        penggunaRepository.deleteAll();
    }

    @Test
    void register_WhenValid_ShouldReturn201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .nama("Pendaki Anyar")
                .email("anyar@example.com")
                .password("password123")
                .levelFisik(LevelFisik.PEMULA)
                .kontakDarurat("08123456789")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email").value("anyar@example.com"))
                .andExpect(jsonPath("$.data.peran").value("PENDAKI"));
    }

    @Test
    void register_WhenDuplicateEmail_ShouldReturn400() throws Exception {
        penggunaRepository.save(Pengguna.builder()
                .nama("User Lama")
                .email("lama@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.PEMULA)
                .kontakDarurat("08123456789")
                .build());

        RegisterRequest request = RegisterRequest.builder()
                .nama("User Baru")
                .email("lama@example.com")
                .password("password123")
                .levelFisik(LevelFisik.PEMULA)
                .kontakDarurat("08123456789")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_WhenInvalidEmailOrPassword_ShouldReturn400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .nama("")
                .email("invalid-email")
                .password("")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_WhenValid_ShouldReturn200() throws Exception {
        penggunaRepository.save(Pengguna.builder()
                .nama("User Uji")
                .email("uji@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MENENGAH)
                .kontakDarurat("08123456789")
                .build());

        LoginRequest request = LoginRequest.builder()
                .email("uji@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email").value("uji@example.com"));
    }

    @Test
    void login_WhenWrongPassword_ShouldReturn401() throws Exception {
        penggunaRepository.save(Pengguna.builder()
                .nama("User Uji")
                .email("uji@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MENENGAH)
                .kontakDarurat("08123456789")
                .build());

        LoginRequest request = LoginRequest.builder()
                .email("uji@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
