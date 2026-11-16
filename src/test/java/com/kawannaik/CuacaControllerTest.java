package com.kawannaik;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kawannaik.model.Gunung;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import com.kawannaik.repository.TitikJalurRepository;
import com.kawannaik.service.impl.CuacaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class CuacaControllerTest {

    @TestConfiguration
    static class MockBmkgConfig {
        @Bean
        @Primary
        CuacaServiceImpl cuacaService(GunungRepository gunungRepository) {
            RestClient.Builder builder = RestClient.builder();
            MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ObjectNode lokasi = root.putObject("lokasi");
            lokasi.put("desa", "Sumberjati");
            lokasi.put("kecamatan", "Tempeh");
            lokasi.put("kotkab", "Lumajang");
            ArrayNode data = root.putArray("data");
            ObjectNode item = data.addObject();
            ArrayNode cuaca = item.putArray("cuaca");
            ArrayNode hari = cuaca.addArray();
            ObjectNode slot = hari.addObject();
            slot.put("local_datetime", "2026-11-02 19:00:00");
            slot.put("t", 26);
            slot.put("hu", 88);
            slot.put("weather_desc", "Berawan");
            slot.put("ws", 3.7);
            slot.put("wd", "SW");
            server.expect(requestTo("https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4=35.08.05.2002"))
                    .andRespond(withSuccess(root.toString(), MediaType.APPLICATION_JSON));
            return new CuacaServiceImpl(gunungRepository, builder);
        }
    }

    @Autowired
    private MockMvc mockMvc;

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
    void getCuaca_WhenKodeWilayahAda_ShouldReturnBmkg() throws Exception {
        Gunung gunung = gunungRepository.save(Gunung.builder()
                .nama("Gunung Semeru")
                .lokasi("Lumajang, Jawa Timur")
                .ketinggianMdpl(3676)
                .kodeWilayah("35.08.05.2002")
                .build());

        mockMvc.perform(get("/api/v1/gunung/" + gunung.getId() + "/cuaca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.desa").value("Sumberjati"))
                .andExpect(jsonPath("$.data.sumber").value("BMKG (Badan Meteorologi, Klimatologi, dan Geofisika)"))
                .andExpect(jsonPath("$.data.prakiraan[0].kondisi").value("Berawan"))
                .andExpect(jsonPath("$.data.prakiraan[0].suhuC").value(26));
    }

    @Test
    void getCuaca_WhenKodeWilayahKosong_ShouldReturn400() throws Exception {
        Gunung gunung = gunungRepository.save(Gunung.builder()
                .nama("Gunung Tanpa Kode")
                .lokasi("Jawa Timur")
                .ketinggianMdpl(1000)
                .build());

        mockMvc.perform(get("/api/v1/gunung/" + gunung.getId() + "/cuaca"))
                .andExpect(status().isBadRequest());
    }
}
