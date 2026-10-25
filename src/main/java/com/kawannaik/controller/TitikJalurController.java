package com.kawannaik.controller;

import com.kawannaik.dto.request.TitikJalurRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.TitikJalurResponse;
import com.kawannaik.service.TitikJalurService;
import com.kawannaik.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "3. Titik Jalur / Waypoints", description = "Manajemen titik-titik pos dan waypoint penting pada jalur pendakian")
@RestController
@RequestMapping("/api/v1")
public class TitikJalurController {

    private final TitikJalurService titikJalurService;

    public TitikJalurController(TitikJalurService titikJalurService) {
        this.titikJalurService = titikJalurService;
    }

    // Public endpoints
    @Operation(summary = "Daftar titik penting jalur", description = "Melihat seluruh titik pos/waypoint pada jalur pendakian tertentu")
    @GetMapping("/titik-jalur/jalur/{jalurId}")
    public ResponseEntity<ApiResponse<List<TitikJalurResponse>>> getTitikByJalurId(
            @PathVariable Long jalurId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<TitikJalurResponse> list = titikJalurService.getTitikByJalurId(jalurId);
        return ResponseEntity.ok(PaginationUtil.paginate(list, page, size, "Titik penting jalur berhasil diambil"));
    }

    // Admin endpoints (Use case 13: Kelola Titik Penting)
    @Operation(summary = "Tambah titik penting (Admin)", description = "Menambahkan pos/waypoint baru pada jalur pendakian")
    @PostMapping("/admin/titik-jalur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TitikJalurResponse>> createTitikJalur(@Valid @RequestBody TitikJalurRequest request) {
        TitikJalurResponse response = titikJalurService.createTitikJalur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Titik penting berhasil ditambahkan"));
    }

    @Operation(summary = "Import GPX (Admin)", description = "Mengganti titik jalur dengan trackpoint dari file GPX")
    @PostMapping(value = "/admin/jalur/{jalurId}/gpx", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> importGpx(
            @PathVariable Long jalurId, @RequestParam("file") MultipartFile file) throws java.io.IOException {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!filename.endsWith(".gpx")) {
            throw new IllegalArgumentException("File harus berekstensi .gpx");
        }
        int count = titikJalurService.importGpx(jalurId, new String(file.getBytes(), StandardCharsets.UTF_8));
        return ResponseEntity.ok(ApiResponse.success(count, "GPX berhasil diimport"));
    }

    @Operation(summary = "Perbarui titik penting (Admin)", description = "Memperbarui data pos/waypoint jalur pendakian")
    @PutMapping("/admin/titik-jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TitikJalurResponse>> updateTitikJalur(
            @PathVariable Long id,
            @Valid @RequestBody TitikJalurRequest request) {
        TitikJalurResponse response = titikJalurService.updateTitikJalur(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Titik penting berhasil diperbarui"));
    }

    @Operation(summary = "Hapus titik penting (Admin)", description = "Menghapus pos/waypoint jalur pendakian")
    @DeleteMapping("/admin/titik-jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTitikJalur(@PathVariable Long id) {
        titikJalurService.deleteTitikJalur(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Titik penting berhasil dihapus"));
    }
}
