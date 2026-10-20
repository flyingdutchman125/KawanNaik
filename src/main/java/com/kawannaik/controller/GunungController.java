package com.kawannaik.controller;

import com.kawannaik.dto.request.GunungRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.GunungResponse;
import com.kawannaik.service.GunungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GunungController {

    private final GunungService gunungService;

    // Public endpoints
    @GetMapping("/gunung")
    public ResponseEntity<ApiResponse<List<GunungResponse>>> getAllGunung(
            @RequestParam(required = false) String search) {
        List<GunungResponse> list = gunungService.getAllGunung(search);
        return ResponseEntity.ok(ApiResponse.success(list, "Daftar gunung berhasil diambil"));
    }

    @GetMapping("/gunung/{id}")
    public ResponseEntity<ApiResponse<GunungResponse>> getGunungById(@PathVariable Long id) {
        GunungResponse response = gunungService.getGunungById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Data gunung berhasil diambil"));
    }

    // Admin endpoints (Use case 11: Kelola Data Gunung)
    @PostMapping("/admin/gunung")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GunungResponse>> createGunung(@Valid @RequestBody GunungRequest request) {
        GunungResponse response = gunungService.createGunung(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Data gunung berhasil ditambahkan"));
    }

    @PutMapping("/admin/gunung/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GunungResponse>> updateGunung(
            @PathVariable Long id,
            @Valid @RequestBody GunungRequest request) {
        GunungResponse response = gunungService.updateGunung(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Data gunung berhasil diperbarui"));
    }

    @DeleteMapping("/admin/gunung/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGunung(@PathVariable Long id) {
        gunungService.deleteGunung(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Data gunung berhasil dihapus"));
    }
}
