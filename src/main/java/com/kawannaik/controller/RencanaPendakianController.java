package com.kawannaik.controller;

import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.request.UpdateStatusRencanaRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.RencanaPendakianResponse;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.service.RencanaPendakianService;
import com.kawannaik.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "4. Rencana Pendakian", description = "Pembuatan rencana pendakian, pencarian teman mendaki, dan lifecycle status")
@RestController
@RequestMapping("/api/v1/rencana")
public class RencanaPendakianController {

    private final RencanaPendakianService rencanaService;

    public RencanaPendakianController(RencanaPendakianService rencanaService) {
        this.rencanaService = rencanaService;
    }

    @Operation(summary = "Buat rencana pendakian baru", description = "Membuat rencana pendakian baru (pengguna otomatis menjadi host/pembuat)")
    @PostMapping
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> createRencana(
            Authentication authentication,
            @Valid @RequestBody RencanaPendakianRequest request) {
        RencanaPendakianResponse response = rencanaService.createRencana(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Rencana pendakian berhasil dibuat"));
    }

    @Operation(summary = "Pencarian & daftar rencana pendakian", description = "Pencarian multi-kriteria rencana pendakian terbuka (berdasarkan gunung, level fisik minimum, dan tanggal naik) beserta opsi pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RencanaPendakianResponse>>> getAllRencana(
            @RequestParam(required = false) Long gunungId,
            @RequestParam(required = false) LevelFisik level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<RencanaPendakianResponse> list = rencanaService.getAllRencana(gunungId, level, tanggal);
        return ResponseEntity.ok(PaginationUtil.paginate(list, page, size, "Daftar rencana pendakian berhasil diambil"));
    }

    @Operation(summary = "Detail rencana pendakian", description = "Melihat detail lengkap rencana pendakian beserta daftar peserta yang telah disetujui")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> getRencanaById(@PathVariable Long id) {
        RencanaPendakianResponse response = rencanaService.getRencanaById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Detail rencana pendakian berhasil diambil"));
    }

    @Operation(summary = "Daftar rencana saya", description = "Melihat seluruh rencana pendakian yang dibuat oleh pengguna yang sedang login")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<RencanaPendakianResponse>>> getMyRencana(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<RencanaPendakianResponse> list = rencanaService.getMyRencana(authentication.getName());
        return ResponseEntity.ok(PaginationUtil.paginate(list, page, size, "Daftar rencana saya berhasil diambil"));
    }

    /**
     * PUT /api/v1/rencana/{id}/status
     * Update status rencana (DIBUKA → DITUTUP → SELESAI / DIBATALKAN).
     */
    @Operation(summary = "Update status rencana", description = "Mengubah status rencana (DIBUKA, DITUTUP, SELESAI, DIBATALKAN) oleh pembuat rencana")
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
    @Operation(summary = "Batalkan rencana pendakian", description = "Membatalkan rencana pendakian yang telah dibuat oleh pemilik/host")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RencanaPendakianResponse>> cancelRencana(
            Authentication authentication,
            @PathVariable Long id) {
        RencanaPendakianResponse response = rencanaService.cancelRencana(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(response, "Rencana pendakian berhasil dibatalkan"));
    }
}

