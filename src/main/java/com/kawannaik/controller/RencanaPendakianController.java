package com.kawannaik.controller;

import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.request.UpdateStatusRencanaRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.RencanaPendakianResponse;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.service.RencanaPendakianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rencana")
@RequiredArgsConstructor
public class RencanaPendakianController {

    private final RencanaPendakianService rencanaService;

    @PostMapping
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> createRencana(
            Authentication authentication,
            @Valid @RequestBody RencanaPendakianRequest request) {
        RencanaPendakianResponse response = rencanaService.createRencana(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Rencana pendakian berhasil dibuat"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RencanaPendakianResponse>>> getAllRencana(
            @RequestParam(required = false) Long gunungId,
            @RequestParam(required = false) LevelFisik level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal) {
        List<RencanaPendakianResponse> list = rencanaService.getAllRencana(gunungId, level, tanggal);
        return ResponseEntity.ok(ApiResponse.success(list, "Daftar rencana pendakian berhasil diambil"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> getRencanaById(@PathVariable Long id) {
        RencanaPendakianResponse response = rencanaService.getRencanaById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Detail rencana pendakian berhasil diambil"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RencanaPendakianResponse>>> getMyRencana(Authentication authentication) {
        List<RencanaPendakianResponse> list = rencanaService.getMyRencana(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(list, "Daftar rencana saya berhasil diambil"));
    }

    /**
     * PUT /api/v1/rencana/{id}/status
     * Update status rencana (DIBUKA → DITUTUP → SELESAI / DIBATALKAN).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> updateStatusRencana(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRencanaRequest request) {
        RencanaPendakianResponse response = rencanaService.updateStatusRencana(
                authentication.getName(), id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(response, "Status rencana pendakian berhasil diperbarui"));
    }

    /**
     * DELETE /api/v1/rencana/{id}/cancel
     * Pembatalan rencana oleh pemilik/host.
     */
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> cancelRencana(
            Authentication authentication,
            @PathVariable Long id) {
        RencanaPendakianResponse response = rencanaService.cancelRencana(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(response, "Rencana pendakian berhasil dibatalkan"));
    }
}

