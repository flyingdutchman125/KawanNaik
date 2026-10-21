package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.GunungRequest;
import com.kawannaik.model.Gunung;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import com.kawannaik.repository.TitikJalurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GunungController using MockMvc + H2 Database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class GunungControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GunungRepository gunungRepository;

    @Autowired
    private JalurRepository jalurRepository;

    @Autowired
    private TitikJalurRepository titikJalurRepository;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    @Autowired
    private PesertaRencanaRepository pesertaRepository;

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        titikJalurRepository.deleteAll();
        jalurRepository.deleteAll();
        gunungRepository.deleteAll();
    }

    @Test
    void getAllGunung_WhenPublic_ShouldReturn200() throws Exception {
        gunungRepository.save(Gunung.builder()
                .nama("Gunung Semeru")
                .lokasi("Lumajang, Jawa Timur")
                .ketinggianMdpl(3676)
                .latitude(-8.108)
                .longitude(112.922)
                .build());

        mockMvc.perform(get("/api/v1/gunung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nama").value("Gunung Semeru"));
    }

    @Test
    void getGunungById_WhenExists_ShouldReturn200() throws Exception {
        Gunung saved = gunungRepository.save(Gunung.builder()
                .nama("Gunung Arjuno")
                .lokasi("Malang, Jawa Timur")
                .ketinggianMdpl(3339)
                .build());

        mockMvc.perform(get("/api/v1/gunung/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nama").value("Gunung Arjuno"));
    }

    @Test
    void getGunungById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/gunung/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createGunung_AsAdmin_ShouldReturn201() throws Exception {
        GunungRequest request = GunungRequest.builder()
                .nama("Gunung Welirang")
                .lokasi("Pasuruan, Jawa Timur")
                .ketinggianMdpl(3156)
                .latitude(-7.766)
                .longitude(112.577)
                .build();

        mockMvc.perform(post("/api/v1/admin/gunung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nama").value("Gunung Welirang"))
                .andExpect(jsonPath("$.data.ketinggianMdpl").value(3156));
    }

    @Test
    void createGunung_WithoutAuth_ShouldReturn401() throws Exception {
        GunungRequest request = GunungRequest.builder()
                .nama("Gunung Test")
                .lokasi("Test")
                .ketinggianMdpl(1000)
                .build();

        mockMvc.perform(post("/api/v1/admin/gunung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PENDAKI")
    void createGunung_AsPendaki_ShouldReturn403() throws Exception {
        GunungRequest request = GunungRequest.builder()
                .nama("Gunung Test")
                .lokasi("Test")
                .ketinggianMdpl(1000)
                .build();

        mockMvc.perform(post("/api/v1/admin/gunung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createGunung_WithInvalidData_ShouldReturn400() throws Exception {
        GunungRequest request = GunungRequest.builder()
                .nama("")  // blank — should fail validation
                .lokasi("")
                .ketinggianMdpl(null)
                .build();

        mockMvc.perform(post("/api/v1/admin/gunung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGunung_AsAdmin_ShouldReturn200() throws Exception {
        Gunung saved = gunungRepository.save(Gunung.builder()
                .nama("Gunung Lama")
                .lokasi("Lokasi Lama")
                .ketinggianMdpl(1000)
                .build());

        GunungRequest updateRequest = GunungRequest.builder()
                .nama("Gunung Baru")
                .lokasi("Lokasi Baru")
                .ketinggianMdpl(2000)
                .build();

        mockMvc.perform(put("/api/v1/admin/gunung/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nama").value("Gunung Baru"))
                .andExpect(jsonPath("$.data.ketinggianMdpl").value(2000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteGunung_AsAdmin_ShouldReturn200() throws Exception {
        Gunung saved = gunungRepository.save(Gunung.builder()
                .nama("Gunung Hapus")
                .lokasi("Akan Dihapus")
                .ketinggianMdpl(500)
                .build());

        mockMvc.perform(delete("/api/v1/admin/gunung/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getAllGunung_WithSearchParam_ShouldFilterResults() throws Exception {
        gunungRepository.save(Gunung.builder().nama("Gunung Semeru").lokasi("Lumajang").ketinggianMdpl(3676).build());
        gunungRepository.save(Gunung.builder().nama("Gunung Arjuno").lokasi("Malang").ketinggianMdpl(3339).build());

        mockMvc.perform(get("/api/v1/gunung").param("search", "Semeru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nama").value("Gunung Semeru"));
    }
}
