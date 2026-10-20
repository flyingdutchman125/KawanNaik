package com.kawannaik.controller;

import com.kawannaik.dto.request.PesertaRencanaRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.PesertaRencanaResponse;
import com.kawannaik.service.PesertaRencanaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rencana/{rencanaId}")
@RequiredArgsConstructor
public class PesertaRencanaController {

    private final PesertaRencanaService pesertaService;

    /**
     * POST /api/v1/rencana/{id}/join
     * Ajukan bergabung ke rencana pendakian.
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<PesertaRencanaResponse>> joinRencana(
            Authentication authentication,
            @PathVariable Long rencanaId) {
        PesertaRencanaResponse response = pesertaService.joinRencana(authentication.getName(), rencanaId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Berhasil mengajukan bergabung ke rencana pendakian"));
    }

    /**
     * DELETE /api/v1/rencana/{id}/leave
     * Batalkan ajuan / tinggalkan rencana pendakian.
     */
    @DeleteMapping("/leave")
    public ResponseEntity<ApiResponse<Void>> leaveRencana(
            Authentication authentication,
            @PathVariable Long rencanaId) {
        pesertaService.leaveRencana(authentication.getName(), rencanaId);
        return ResponseEntity.ok(ApiResponse.success(null, "Berhasil meninggalkan rencana pendakian"));
    }

    /**
     * PUT /api/v1/rencana/{id}/peserta/{pesertaId}/status
     * Host menerima / menolak peserta.
     */
    @PutMapping("/peserta/{pesertaId}/status")
    public ResponseEntity<ApiResponse<PesertaRencanaResponse>> updateStatusPeserta(
            Authentication authentication,
            @PathVariable Long rencanaId,
            @PathVariable Long pesertaId,
            @Valid @RequestBody PesertaRencanaRequest request) {
        PesertaRencanaResponse response = pesertaService.updateStatusPeserta(
                authentication.getName(), rencanaId, pesertaId, request.getStatusPermintaan());
        return ResponseEntity.ok(ApiResponse.success(response, "Status peserta berhasil diperbarui"));
    }

    /**
     * GET /api/v1/rencana/{id}/peserta
     * Daftar peserta per rencana pendakian.
     */
    @GetMapping("/peserta")
    public ResponseEntity<ApiResponse<List<PesertaRencanaResponse>>> getPesertaByRencana(
            @PathVariable Long rencanaId) {
        List<PesertaRencanaResponse> list = pesertaService.getPesertaByRencana(rencanaId);
        return ResponseEntity.ok(ApiResponse.success(list, "Daftar peserta berhasil diambil"));
    }
}
