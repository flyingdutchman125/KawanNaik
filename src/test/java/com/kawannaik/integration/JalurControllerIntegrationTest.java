package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.JalurRequest;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.enums.LevelFisik;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class JalurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JalurRepository jalurRepository;

    @Autowired
    private GunungRepository gunungRepository;

    @Autowired
    private TitikJalurRepository titikJalurRepository;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    @Autowired
    private PesertaRencanaRepository pesertaRepository;

    private Gunung rinjani;
    private Jalur sembalun;

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        titikJalurRepository.deleteAll();
        jalurRepository.deleteAll();
        gunungRepository.deleteAll();

        rinjani = gunungRepository.save(Gunung.builder()
                .nama("Gunung Rinjani")
                .lokasi("Lombok, NTB")
                .ketinggianMdpl(3726)
                .build());

        sembalun = jalurRepository.save(Jalur.builder()
                .gunung(rinjani)
                .namaJalur("Sembalun")
                .jarakKm(12.5)
                .tingkatKesulitan("Menengah - Berat")
                .levelMinimum(LevelFisik.MENENGAH)
                .estimasiJam(8.0)
                .build());
    }

    @Test
    void getJalurByGunungId_Public_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/jalur/gunung/" + rinjani.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].namaJalur").value("Sembalun"));
    }

    @Test
    void getJalurById_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/jalur/" + sembalun.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.namaJalur").value("Sembalun"))
                .andExpect(jsonPath("$.data.jarakKm").value(12.5));
    }

    @Test
    void getJalurById_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/jalur/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createJalur_AsAdmin_ShouldReturn201() throws Exception {
        JalurRequest request = JalurRequest.builder()
                .gunungId(rinjani.getId())
                .namaJalur("Senaru")
                .jarakKm(10.0)
                .tingkatKesulitan("Menengah")
                .levelMinimum(LevelFisik.PEMULA)
                .estimasiJam(7.0)
                .build();

        mockMvc.perform(post("/api/v1/admin/jalur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.namaJalur").value("Senaru"));
    }

    @Test
    @WithMockUser(roles = "PENDAKI")
    void createJalur_AsPendaki_ShouldReturn403() throws Exception {
        JalurRequest request = JalurRequest.builder()
                .gunungId(rinjani.getId())
                .namaJalur("Torean")
                .build();

        mockMvc.perform(post("/api/v1/admin/jalur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateJalur_AsAdmin_ShouldReturn200() throws Exception {
        JalurRequest request = JalurRequest.builder()
                .gunungId(rinjani.getId())
                .namaJalur("Sembalun Indah")
                .jarakKm(13.0)
                .tingkatKesulitan("Berat")
                .levelMinimum(LevelFisik.MAHIR)
                .estimasiJam(9.0)
                .build();

        mockMvc.perform(put("/api/v1/admin/jalur/" + sembalun.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.namaJalur").value("Sembalun Indah"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteJalur_AsAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/jalur/" + sembalun.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
