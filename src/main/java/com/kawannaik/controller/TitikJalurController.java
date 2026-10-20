package com.kawannaik.controller;

import com.kawannaik.dto.request.TitikJalurRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.TitikJalurResponse;
import com.kawannaik.service.TitikJalurService;
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
public class TitikJalurController {

    private final TitikJalurService titikJalurService;

    // Public endpoints
    @GetMapping("/titik-jalur/jalur/{jalurId}")
    public ResponseEntity<ApiResponse<List<TitikJalurResponse>>> getTitikByJalurId(@PathVariable Long jalurId) {
        List<TitikJalurResponse> list = titikJalurService.getTitikByJalurId(jalurId);
        return ResponseEntity.ok(ApiResponse.success(list, "Titik penting jalur berhasil diambil"));
    }

    // Admin endpoints (Use case 13: Kelola Titik Penting)
    @PostMapping("/admin/titik-jalur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TitikJalurResponse>> createTitikJalur(@Valid @RequestBody TitikJalurRequest request) {
        TitikJalurResponse response = titikJalurService.createTitikJalur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Titik penting berhasil ditambahkan"));
    }

    @PutMapping("/admin/titik-jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TitikJalurResponse>> updateTitikJalur(
            @PathVariable Long id,
            @Valid @RequestBody TitikJalurRequest request) {
        TitikJalurResponse response = titikJalurService.updateTitikJalur(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Titik penting berhasil diperbarui"));
    }

    @DeleteMapping("/admin/titik-jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTitikJalur(@PathVariable Long id) {
        titikJalurService.deleteTitikJalur(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Titik penting berhasil dihapus"));
    }
}
