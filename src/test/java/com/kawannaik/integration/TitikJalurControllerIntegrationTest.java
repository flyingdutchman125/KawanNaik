package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.TitikJalurRequest;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.TitikJalur;
import com.kawannaik.model.enums.JenisTitik;
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
class TitikJalurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TitikJalurRepository titikJalurRepository;

    @Autowired
    private JalurRepository jalurRepository;

    @Autowired
    private GunungRepository gunungRepository;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    @Autowired
    private PesertaRencanaRepository pesertaRepository;

    private Jalur jalur;
    private TitikJalur pos1;

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        titikJalurRepository.deleteAll();
        jalurRepository.deleteAll();
        gunungRepository.deleteAll();

        Gunung merbabu = gunungRepository.save(Gunung.builder()
                .nama("Gunung Merbabu")
                .lokasi("Magelang")
                .ketinggianMdpl(3145)
                .build());

        jalur = jalurRepository.save(Jalur.builder()
                .gunung(merbabu)
                .namaJalur("Selo")
                .levelMinimum(LevelFisik.PEMULA)
                .estimasiJam(6.0)
                .build());

        pos1 = titikJalurRepository.save(TitikJalur.builder()
                .jalur(jalur)
                .nama("Pos 1 Dok-dok")
                .jenis(JenisTitik.POS)
                .urutan(1)
                .deskripsi("Tempat istirahat awal")
                .build());
    }

    @Test
    void getTitikByJalurId_Public_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/titik-jalur/jalur/" + jalur.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nama").value("Pos 1 Dok-dok"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTitikJalur_AsAdmin_ShouldReturn201() throws Exception {
        TitikJalurRequest request = TitikJalurRequest.builder()
                .jalurId(jalur.getId())
                .nama("Pos 2 Selo")
                .jenis(JenisTitik.POS)
                .urutan(2)
                .deskripsi("Tempat mendirikan tenda alternatif")
                .build();

        mockMvc.perform(post("/api/v1/admin/titik-jalur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nama").value("Pos 2 Selo"));
    }

    @Test
    @WithMockUser(roles = "PENDAKI")
    void createTitikJalur_AsPendaki_ShouldReturn403() throws Exception {
        TitikJalurRequest request = TitikJalurRequest.builder()
                .jalurId(jalur.getId())
                .nama("Pos 3")
                .jenis(JenisTitik.POS)
                .urutan(3)
                .build();

        mockMvc.perform(post("/api/v1/admin/titik-jalur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTitikJalur_AsAdmin_ShouldReturn200() throws Exception {
        TitikJalurRequest request = TitikJalurRequest.builder()
                .jalurId(jalur.getId())
                .nama("Pos 1 Dok-dok Diperbarui")
                .jenis(JenisTitik.POS)
                .urutan(1)
                .deskripsi("Pos istirahat dengan shelter baru")
                .build();

        mockMvc.perform(put("/api/v1/admin/titik-jalur/" + pos1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nama").value("Pos 1 Dok-dok Diperbarui"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTitikJalur_AsAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/titik-jalur/" + pos1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
