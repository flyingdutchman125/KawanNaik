package com.kawannaik.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.kawannaik.dto.response.CuacaResponse;
import com.kawannaik.exception.BadRequestException;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Gunung;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.service.CuacaService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy prakiraan cuaca BMKG.
 * API publik: https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4={kode wilayah tingkat IV}
 * ponytail: tanpa cache. BMKG batas 60 req/menit/IP. Tambah cache kalau traffic naik.
 */
@Service
public class CuacaServiceImpl implements CuacaService {

    private static final String BMKG_URL = "https://api.bmkg.go.id/publik/prakiraan-cuaca?adm4={adm4}";

    private final GunungRepository gunungRepository;
    private final RestClient restClient;

    public CuacaServiceImpl(GunungRepository gunungRepository, RestClient.Builder restClientBuilder) {
        this.gunungRepository = gunungRepository;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public CuacaResponse getCuacaByGunung(Long gunungId) {
        Gunung gunung = gunungRepository.findById(gunungId)
                .orElseThrow(() -> new ResourceNotFoundException("Gunung dengan ID " + gunungId + " tidak ditemukan"));

        if (gunung.getKodeWilayah() == null || gunung.getKodeWilayah().isBlank()) {
            throw new BadRequestException("Gunung belum punya kode wilayah BMKG (adm4)");
        }

        JsonNode body;
        try {
            body = restClient.get()
                    .uri(BMKG_URL, gunung.getKodeWilayah())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException ex) {
            throw new BadRequestException("Gagal mengambil data BMKG: " + ex.getMessage());
        }

        if (body == null || !body.has("data")) {
            throw new BadRequestException("Respons BMKG tidak valid");
        }

        CuacaResponse response = new CuacaResponse();
        response.setGunungId(gunung.getId());
        response.setNamaGunung(gunung.getNama());
        response.setKodeWilayah(gunung.getKodeWilayah());

        JsonNode lokasi = body.path("lokasi");
        response.setDesa(lokasi.path("desa").asText(null));
        response.setKecamatan(lokasi.path("kecamatan").asText(null));
        response.setKota(lokasi.path("kotkab").asText(null));

        List<CuacaResponse.Slot> slots = new ArrayList<>();
        for (JsonNode hari : body.path("data").path(0).path("cuaca")) {
            for (JsonNode slot : hari) {
                slots.add(new CuacaResponse.Slot(
                        slot.path("local_datetime").asText(null),
                        slot.path("t").isNumber() ? slot.path("t").asInt() : null,
                        slot.path("hu").isNumber() ? slot.path("hu").asInt() : null,
                        slot.path("weather_desc").asText(null),
                        slot.path("ws").isNumber() ? slot.path("ws").asDouble() : null,
                        slot.path("wd").asText(null)));
            }
        }
        response.setPrakiraan(slots);
        return response;
    }
}
