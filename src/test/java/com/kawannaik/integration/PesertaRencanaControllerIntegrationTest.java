package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.PesertaRencanaRequest;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.PesertaRencana;
import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import com.kawannaik.model.enums.StatusPermintaan;
import com.kawannaik.model.enums.StatusRencana;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class PesertaRencanaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PesertaRencanaRepository pesertaRepository;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    @Autowired
    private JalurRepository jalurRepository;

    @Autowired
    private GunungRepository gunungRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    private Pengguna host;
    private Pengguna pendaki;
    private RencanaPendakian rencana;

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        jalurRepository.deleteAll();
        gunungRepository.deleteAll();
        penggunaRepository.deleteAll();

        host = penggunaRepository.save(Pengguna.builder()
                .nama("Host")
                .email("host@example.com")
                .passwordHash("hashed")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .kontakDarurat("0811111111")
                .build());

        pendaki = penggunaRepository.save(Pengguna.builder()
                .nama("Pendaki Keren")
                .email("pendaki@example.com")
                .passwordHash("hashed")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .kontakDarurat("0822222222")
                .build());

        Gunung merbabu = gunungRepository.save(Gunung.builder()
                .nama("Gunung Merbabu")
                .lokasi("Magelang")
                .ketinggianMdpl(3145)
                .build());

        Jalur selo = jalurRepository.save(Jalur.builder()
                .gunung(merbabu)
                .namaJalur("Selo")
                .levelMinimum(LevelFisik.PEMULA)
                .estimasiJam(6.0)
                .build());

        rencana = rencanaRepository.save(RencanaPendakian.builder()
                .pembuat(host)
                .jalur(selo)
                .tanggalNaik(LocalDate.now().plusDays(5))
                .kuota(5)
                .levelMinimum(LevelFisik.PEMULA)
                .status(StatusRencana.DIBUKA)
                .build());
    }

    @Test
    @WithMockUser(username = "pendaki@example.com", roles = "PENDAKI")
    void joinRencana_Authenticated_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/api/v1/rencana/" + rencana.getId() + "/join"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("MENUNGGU"));
    }

    @Test
    void joinRencana_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/rencana/" + rencana.getId() + "/join"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "pendaki@example.com", roles = "PENDAKI")
    void leaveRencana_WhenParticipant_ShouldReturn200() throws Exception {
        pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.MENUNGGU)
                .build());

        mockMvc.perform(delete("/api/v1/rencana/" + rencana.getId() + "/leave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "host@example.com", roles = "PENDAKI")
    void updateStatusPeserta_AsHost_ShouldReturn200() throws Exception {
        PesertaRencana peserta = pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.MENUNGGU)
                .build());

        PesertaRencanaRequest request = new PesertaRencanaRequest(StatusPermintaan.DITERIMA);

        mockMvc.perform(put("/api/v1/rencana/" + rencana.getId() + "/peserta/" + peserta.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DITERIMA"));
    }

    @Test
    void getPesertaByRencana_Public_ShouldReturn200() throws Exception {
        pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.DITERIMA)
                .build());

        mockMvc.perform(get("/api/v1/rencana/" + rencana.getId() + "/peserta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
