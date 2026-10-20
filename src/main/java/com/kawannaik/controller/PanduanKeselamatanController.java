package com.kawannaik.controller;

import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.PanduanKeselamatanResponse;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.service.PanduanKeselamatanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/panduan")
@RequiredArgsConstructor
public class PanduanKeselamatanController {

    private final PanduanKeselamatanService panduanService;

    /**
     * GET /api/v1/panduan
     * Public: daftar panduan keselamatan dengan filter opsional kategori dan level.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PanduanKeselamatanResponse>>> getAllPanduan(
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) LevelFisik level) {
        List<PanduanKeselamatanResponse> list = panduanService.getAllPanduan(kategori, level);
        return ResponseEntity.ok(ApiResponse.success(list, "Daftar panduan keselamatan berhasil diambil"));
    }

    /**
     * GET /api/v1/panduan/{id}
     * Public: detail panduan keselamatan.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PanduanKeselamatanResponse>> getPanduanById(@PathVariable Long id) {
        PanduanKeselamatanResponse response = panduanService.getPanduanById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Detail panduan keselamatan berhasil diambil"));
    }
}
