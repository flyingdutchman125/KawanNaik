package com.kawannaik.integration;

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
import com.kawannaik.repository.RencanaPendakianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=true",
        "springdoc.swagger-ui.enabled=true"
})
@Transactional
class PaginationAndOpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GunungRepository gunungRepository;

    @Autowired
    private JalurRepository jalurRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private RencanaPendakianRepository rencanaRepository;

    private Gunung semeru;
    private Gunung rinjani;

    @BeforeEach
    void setUp() {
        semeru = gunungRepository.save(Gunung.builder()
                .nama("Gunung Semeru")
                .ketinggianMdpl(3676)
                .lokasi("Lumajang, Jawa Timur")
                .build());

        rinjani = gunungRepository.save(Gunung.builder()
                .nama("Gunung Rinjani")
                .ketinggianMdpl(3726)
                .lokasi("Lombok, NTB")
                .build());

        Jalur ranuPani = jalurRepository.save(Jalur.builder()
                .namaJalur("Ranu Pani")
                .gunung(semeru)
                .estimasiJam(10.0)
                .levelMinimum(LevelFisik.MENENGAH)
                .build());

        Pengguna host = penggunaRepository.save(Pengguna.builder()
                .nama("Host Semeru")
                .email("host@example.com")
                .passwordHash("password")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .build());

        rencanaRepository.save(RencanaPendakian.builder()
                .pembuat(host)
                .jalur(ranuPani)
                .tanggalNaik(LocalDate.now().plusDays(10))
                .kuota(5)
                .levelMinimum(LevelFisik.MENENGAH)
                .catatan("Kumpul di basecamp")
                .status(StatusRencana.DIBUKA)
                .build());
    }

    @Test
    @DisplayName("OpenAPI JSON endpoint harus dapat diakses secara publik tanpa token")
    void openApiDocs_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("KawanNaik API"));
    }

    @Test
    @DisplayName("Swagger UI HTML endpoint harus dapat diakses secara publik")
    void swaggerUi_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Pagination pada endpoint /api/v1/gunung harus mengembalikan metadata page, size, totalElements")
    void getGunung_WithPagination_ShouldReturnPaginatedResponse() throws Exception {
        mockMvc.perform(get("/api/v1/gunung")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(2)));
    }

    @Test
    @DisplayName("Pencarian multi-kriteria rencana pendakian dengan pagination")
    void getRencana_WithFilterAndPagination_ShouldReturnFilteredPage() throws Exception {
        mockMvc.perform(get("/api/v1/rencana")
                        .param("gunungId", semeru.getId().toString())
                        .param("level", "MENENGAH")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
