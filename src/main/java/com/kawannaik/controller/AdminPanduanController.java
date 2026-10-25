package com.kawannaik.controller;

import com.kawannaik.dto.request.PanduanKeselamatanRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.PanduanKeselamatanResponse;
import com.kawannaik.service.PanduanKeselamatanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6. Panduan Keselamatan (Admin)", description = "Manajemen CRUD master data panduan keselamatan oleh administrator")
@RestController
@RequestMapping("/api/v1/admin/panduan")
public class AdminPanduanController {

    private final PanduanKeselamatanService panduanService;

    public AdminPanduanController(PanduanKeselamatanService panduanService) {
        this.panduanService = panduanService;
    }

    /**
     * POST /api/v1/admin/panduan
     * Admin: buat panduan keselamatan baru.
     */
    @Operation(summary = "Tambah panduan keselamatan (Admin)", description = "Membuat artikel panduan keselamatan baru")
    @PostMapping
    public ResponseEntity<ApiResponse<PanduanKeselamatanResponse>> createPanduan(
            @Valid @RequestBody PanduanKeselamatanRequest request) {
        PanduanKeselamatanResponse response = panduanService.createPanduan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Panduan keselamatan berhasil dibuat"));
    }

    /**
     * PUT /api/v1/admin/panduan/{id}
     * Admin: update panduan keselamatan.
     */
    @Operation(summary = "Perbarui panduan keselamatan (Admin)", description = "Memperbarui artikel panduan keselamatan")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PanduanKeselamatanResponse>> updatePanduan(
            @PathVariable Long id,
            @Valid @RequestBody PanduanKeselamatanRequest request) {
        PanduanKeselamatanResponse response = panduanService.updatePanduan(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Panduan keselamatan berhasil diperbarui"));
    }

    /**
     * DELETE /api/v1/admin/panduan/{id}
     * Admin: hapus panduan keselamatan.
     */
    @Operation(summary = "Hapus panduan keselamatan (Admin)", description = "Menghapus artikel panduan keselamatan")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePanduan(@PathVariable Long id) {
        panduanService.deletePanduan(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Panduan keselamatan berhasil dihapus"));
    }
}
