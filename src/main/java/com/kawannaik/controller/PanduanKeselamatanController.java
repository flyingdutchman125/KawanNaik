package com.kawannaik.controller;

import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.PanduanKeselamatanResponse;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.service.PanduanKeselamatanService;
import com.kawannaik.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "6. Panduan Keselamatan", description = "Panduan keselamatan dan tips pendakian berdasarkan level fisik dan kategori")
@RestController
@RequestMapping("/api/v1/panduan")
public class PanduanKeselamatanController {

    private final PanduanKeselamatanService panduanService;

    public PanduanKeselamatanController(PanduanKeselamatanService panduanService) {
        this.panduanService = panduanService;
    }

    /**
     * GET /api/v1/panduan
     * Public: daftar panduan keselamatan dengan filter opsional kategori dan level.
     */
    @Operation(summary = "Daftar panduan keselamatan", description = "Melihat panduan keselamatan dengan filter opsional kategori, level fisik, dan pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PanduanKeselamatanResponse>>> getAllPanduan(
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) LevelFisik level,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<PanduanKeselamatanResponse> list = panduanService.getAllPanduan(kategori, level);
        return ResponseEntity.ok(PaginationUtil.paginate(list, page, size, "Daftar panduan keselamatan berhasil diambil"));
    }

    /**
     * GET /api/v1/panduan/{id}
     * Public: detail panduan keselamatan.
     */
    @Operation(summary = "Detail panduan keselamatan", description = "Melihat isi lengkap artikel/panduan keselamatan berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PanduanKeselamatanResponse>> getPanduanById(@PathVariable Long id) {
        PanduanKeselamatanResponse response = panduanService.getPanduanById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Detail panduan keselamatan berhasil diambil"));
    }
}
