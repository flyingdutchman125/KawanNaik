package com.kawannaik.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.request.UpdateStatusRencanaRequest;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
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
class RencanaPendakianControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    @Autowired
    private JalurRepository jalurRepository;

    @Autowired
    private GunungRepository gunungRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PesertaRencanaRepository pesertaRepository;

    private Pengguna host;
    private Gunung semeru;
    private Jalur ranuPani;
    private RencanaPendakian rencana;

    @BeforeEach
    void setUp() {
        pesertaRepository.deleteAll();
        rencanaRepository.deleteAll();
        jalurRepository.deleteAll();
        gunungRepository.deleteAll();
        penggunaRepository.deleteAll();

        host = penggunaRepository.save(Pengguna.builder()
                .nama("Host Pendaki")
                .email("host@example.com")
                .passwordHash("hashed")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .kontakDarurat("08123456789")
                .build());

        semeru = gunungRepository.save(Gunung.builder()
                .nama("Gunung Semeru")
                .lokasi("Lumajang")
                .ketinggianMdpl(3676)
                .build());

        ranuPani = jalurRepository.save(Jalur.builder()
                .gunung(semeru)
                .namaJalur("Ranu Pani")
                .levelMinimum(LevelFisik.MENENGAH)
                .estimasiJam(10.0)
                .build());

        rencana = rencanaRepository.save(RencanaPendakian.builder()
                .pembuat(host)
                .jalur(ranuPani)
                .tanggalNaik(LocalDate.now().plusDays(7))
                .kuota(5)
                .levelMinimum(LevelFisik.MENENGAH)
                .catatan("Kumpul di basecamp")
                .status(StatusRencana.DIBUKA)
                .build());
    }

    @Test
    void getAllRencana_Public_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/rencana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].namaGunung").value("Gunung Semeru"))
                .andExpect(jsonPath("$.data[0].namaJalur").value("Ranu Pani"));
    }

    @Test
    void getAllRencana_WithFilter_ShouldFilterCorrectly() throws Exception {
        // Multi-criteria filter matching existing rencana
        mockMvc.perform(get("/api/v1/rencana")
                        .param("gunungId", semeru.getId().toString())
                        .param("level", "MENENGAH")
                        .param("tanggal", rencana.getTanggalNaik().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));

        // Multi-criteria filter not matching (different level)
        mockMvc.perform(get("/api/v1/rencana")
                        .param("gunungId", semeru.getId().toString())
                        .param("level", "PEMULA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void getRencanaById_WhenExists_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/rencana/" + rencana.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(rencana.getId()))
                .andExpect(jsonPath("$.data.namaPembuat").value("Host Pendaki"));
    }

    @Test
    void getRencanaById_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/rencana/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "host@example.com", roles = "PENDAKI")
    void createRencana_Authenticated_ShouldReturn201() throws Exception {
        RencanaPendakianRequest request = RencanaPendakianRequest.builder()
                .jalurId(ranuPani.getId())
                .tanggalNaik(LocalDate.now().plusDays(14))
                .kuota(4)
                .levelMinimum(LevelFisik.MAHIR)
                .catatan("Siapkan perlengkapan camping")
                .build();

        mockMvc.perform(post("/api/v1/rencana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.kuota").value(4))
                .andExpect(jsonPath("$.data.status").value("DIBUKA"));
    }

    @Test
    void createRencana_Unauthenticated_ShouldReturn401() throws Exception {
        RencanaPendakianRequest request = RencanaPendakianRequest.builder()
                .jalurId(ranuPani.getId())
                .tanggalNaik(LocalDate.now().plusDays(14))
                .kuota(4)
                .build();

        mockMvc.perform(post("/api/v1/rencana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "host@example.com", roles = "PENDAKI")
    void getMyRencana_Authenticated_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/rencana/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "host@example.com", roles = "PENDAKI")
    void updateStatusRencana_AsHost_ShouldReturn200() throws Exception {
        UpdateStatusRencanaRequest request = new UpdateStatusRencanaRequest();
        request.setStatus(StatusRencana.DITUTUP);

        mockMvc.perform(put("/api/v1/rencana/" + rencana.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DITUTUP"));
    }

    @Test
    @WithMockUser(username = "host@example.com", roles = "PENDAKI")
    void cancelRencana_AsHost_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/v1/rencana/" + rencana.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DIBATALKAN"));
    }
}
