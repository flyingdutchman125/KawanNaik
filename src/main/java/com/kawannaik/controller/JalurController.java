package com.kawannaik.controller;

import com.kawannaik.dto.request.JalurRequest;
import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.JalurResponse;
import com.kawannaik.service.JalurService;
import com.kawannaik.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "3. Jalur Pendakian", description = "Informasi rute jalur pendakian, estimasi waktu, dan level fisik minimum")
@RestController
@RequestMapping("/api/v1")
public class JalurController {

    private final JalurService jalurService;

    public JalurController(JalurService jalurService) {
        this.jalurService = jalurService;
    }

    // Public endpoints
    @Operation(summary = "Daftar jalur pendakian per gunung", description = "Mengambil seluruh jalur pendakian untuk gunung tertentu dengan opsi pagination")
    @GetMapping("/jalur/gunung/{gunungId}")
    public ResponseEntity<ApiResponse<List<JalurResponse>>> getJalurByGunungId(
            @PathVariable Long gunungId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<JalurResponse> list = jalurService.getJalurByGunungId(gunungId);
        return ResponseEntity.ok(PaginationUtil.paginate(list, page, size, "Daftar jalur pendakian berhasil diambil"));
    }

    @Operation(summary = "Detail jalur pendakian", description = "Mengambil informasi detail jalur pendakian beserta daftar titik pos/waypoint")
    @GetMapping("/jalur/{id}")
    public ResponseEntity<ApiResponse<JalurResponse>> getJalurById(@PathVariable Long id) {
        JalurResponse response = jalurService.getJalurById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Data jalur pendakian berhasil diambil"));
    }

    // Admin endpoints (Use case 12: Kelola Data Jalur)
    @Operation(summary = "Tambah jalur baru (Admin)", description = "Menambahkan master data jalur pendakian baru")
    @PostMapping("/admin/jalur")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JalurResponse>> createJalur(@Valid @RequestBody JalurRequest request) {
        JalurResponse response = jalurService.createJalur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Data jalur pendakian berhasil ditambahkan"));
    }

    @Operation(summary = "Perbarui jalur (Admin)", description = "Memperbarui master data jalur pendakian")
    @PutMapping("/admin/jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<JalurResponse>> updateJalur(
            @PathVariable Long id,
            @Valid @RequestBody JalurRequest request) {
        JalurResponse response = jalurService.updateJalur(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Data jalur pendakian berhasil diperbarui"));
    }

    @Operation(summary = "Hapus jalur (Admin)", description = "Menghapus data jalur pendakian")
    @DeleteMapping("/admin/jalur/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJalur(@PathVariable Long id) {
        jalurService.deleteJalur(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Data jalur pendakian berhasil dihapus"));
    }
}
