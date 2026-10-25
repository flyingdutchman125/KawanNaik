package com.kawannaik.controller;

import com.kawannaik.dto.request.GunungRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.GunungResponse;
import com.kawannaik.service.GunungService;
import com.kawannaik.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. Gunung", description = "Katalog informasi gunung dan manajemen data gunung")
@RestController
@RequestMapping("/api/v1")
public class GunungController {

    private final GunungService gunungService;

    public GunungController(GunungService gunungService) {
        this.gunungService = gunungService;
    }

    // Public endpoints
    @Operation(summary = "Daftar gunung", description = "Melihat seluruh gunung dengan filter pencarian nama/lokasi dan pagination opsional")
    @GetMapping("/gunung")
    public ResponseEntity<ApiResponse<List<GunungResponse>>> getAllGunung(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<GunungResponse> list = gunungService.getAllGunung(search);
        return ResponseEntity.ok(PaginationUtil.paginate(list, page, size, "Daftar gunung berhasil diambil"));
    }

    @Operation(summary = "Detail gunung", description = "Melihat detail informasi gunung berdasarkan ID")
    @GetMapping("/gunung/{id}")
    public ResponseEntity<ApiResponse<GunungResponse>> getGunungById(@PathVariable Long id) {
        GunungResponse response = gunungService.getGunungById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Data gunung berhasil diambil"));
    }

    // Admin endpoints (Use case 11: Kelola Data Gunung)
    @Operation(summary = "Tambah gunung baru (Admin)", description = "Menambahkan master data gunung baru")
    @PostMapping("/admin/gunung")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GunungResponse>> createGunung(@Valid @RequestBody GunungRequest request) {
        GunungResponse response = gunungService.createGunung(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Data gunung berhasil ditambahkan"));
    }

    @Operation(summary = "Perbarui data gunung (Admin)", description = "Memperbarui master data gunung berdasarkan ID")
    @PutMapping("/admin/gunung/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GunungResponse>> updateGunung(
            @PathVariable Long id,
            @Valid @RequestBody GunungRequest request) {
        GunungResponse response = gunungService.updateGunung(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Data gunung berhasil diperbarui"));
    }

    @Operation(summary = "Hapus data gunung (Admin)", description = "Menghapus data gunung berdasarkan ID")
    @DeleteMapping("/admin/gunung/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGunung(@PathVariable Long id) {
        gunungService.deleteGunung(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Data gunung berhasil dihapus"));
    }
}
