package com.kawannaik.controller;

import com.kawannaik.dto.request.JalurRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.JalurResponse;
import com.kawannaik.service.JalurService;
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
public class JalurController {

    private final JalurService jalurService;

    // Public endpoints
    @GetMapping("/jalur/gunung/{gunungId}")
    public ResponseEntity<ApiResponse<List<JalurResponse>>> getJalurByGunungId(@PathVariable Long gunungId) {
        List<JalurResponse> list = jalurService.getJalurByGunungId(gunungId);
        return ResponseEntity.ok(ApiResponse.success(list, "Daftar jalur pendakian berhasil diambil"));
    }

    @GetMapping("/jalur/{id}")
    public ResponseEntity<ApiResponse<JalurResponse>> getJalurById(@PathVariable Long id) {
        JalurResponse response = jalurService.getJalurById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Data jalur pendakian berhasil diambil"));
    }

    // Admin endpoints (Use case 12: Kelola Data Jalur)
    @PostMapping("/admin/jalur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JalurResponse>> createJalur(@Valid @RequestBody JalurRequest request) {
        JalurResponse response = jalurService.createJalur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Data jalur pendakian berhasil ditambahkan"));
    }

    @PutMapping("/admin/jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JalurResponse>> updateJalur(
            @PathVariable Long id,
            @Valid @RequestBody JalurRequest request) {
        JalurResponse response = jalurService.updateJalur(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Data jalur pendakian berhasil diperbarui"));
    }

    @DeleteMapping("/admin/jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJalur(@PathVariable Long id) {
        jalurService.deleteJalur(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Data jalur pendakian berhasil dihapus"));
    }
}
