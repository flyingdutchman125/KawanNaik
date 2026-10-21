package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.PanduanKeselamatanRequest;
import com.kawannaik.model.PanduanKeselamatan;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.repository.PanduanKeselamatanRepository;
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
class PanduanKeselamatanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PanduanKeselamatanRepository panduanRepository;

    private PanduanKeselamatan savedPanduan;

    @BeforeEach
    void setUp() {
        panduanRepository.deleteAll();

        savedPanduan = panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Tips Mengatasi Hipotermia")
                .isi("Segera ganti pakaian basah dengan yang kering.")
                .kategori("MEDIS")
                .levelMinimum(LevelFisik.PEMULA)
                .build());
    }

    @Test
    void getAllPanduan_Public_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/panduan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].judul").value("Tips Mengatasi Hipotermia"));
    }

    @Test
    void getAllPanduan_WithFilter_ShouldFilterCorrectly() throws Exception {
        mockMvc.perform(get("/api/v1/panduan")
                        .param("kategori", "MEDIS")
                        .param("level", "PEMULA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));

        mockMvc.perform(get("/api/v1/panduan")
                        .param("kategori", "SURVIVAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void getPanduanById_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/panduan/" + savedPanduan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.judul").value("Tips Mengatasi Hipotermia"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPanduan_AsAdmin_ShouldReturn201() throws Exception {
        PanduanKeselamatanRequest request = PanduanKeselamatanRequest.builder()
                .judul("Manajemen Logistik Pendakian")
                .isi("Hitung kebutuhan kalori harian minimal 3000 kcal.")
                .kategori("LOGISTIK")
                .levelMinimum(LevelFisik.MENENGAH)
                .build();

        mockMvc.perform(post("/api/v1/admin/panduan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.judul").value("Manajemen Logistik Pendakian"));
    }

    @Test
    @WithMockUser(roles = "PENDAKI")
    void createPanduan_AsPendaki_ShouldReturn403() throws Exception {
        PanduanKeselamatanRequest request = PanduanKeselamatanRequest.builder()
                .judul("Tips Mendaki")
                .isi("Bawa air cukup.")
                .build();

        mockMvc.perform(post("/api/v1/admin/panduan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePanduan_AsAdmin_ShouldReturn200() throws Exception {
        PanduanKeselamatanRequest request = PanduanKeselamatanRequest.builder()
                .judul("Tips Mengatasi Hipotermia Akut")
                .isi("Segera berikan minuman hangat.")
                .kategori("MEDIS")
                .levelMinimum(LevelFisik.PEMULA)
                .build();

        mockMvc.perform(put("/api/v1/admin/panduan/" + savedPanduan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.judul").value("Tips Mengatasi Hipotermia Akut"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePanduan_AsAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/panduan/" + savedPanduan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
