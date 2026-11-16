package com.kawannaik.controller;

import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.CuacaResponse;
import com.kawannaik.service.CuacaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2. Gunung", description = "Katalog informasi gunung dan manajemen data gunung")
@RestController
@RequestMapping("/api/v1/gunung")
public class CuacaController {

    private final CuacaService cuacaService;

    public CuacaController(CuacaService cuacaService) {
        this.cuacaService = cuacaService;
    }

    @Operation(summary = "Prakiraan cuaca gunung", description = "Prakiraan 3 hari dari BMKG berdasarkan kode wilayah desa gunung. Sumber: BMKG.")
    @GetMapping("/{id}/cuaca")
    public ResponseEntity<ApiResponse<CuacaResponse>> getCuaca(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cuacaService.getCuacaByGunung(id), "Prakiraan cuaca BMKG berhasil diambil"));
    }
}
