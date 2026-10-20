package com.kawannaik.service;

import com.kawannaik.dto.request.PanduanKeselamatanRequest;
import com.kawannaik.dto.response.PanduanKeselamatanResponse;
import com.kawannaik.model.enums.LevelFisik;

import java.util.List;

public interface PanduanKeselamatanService {

    /**
     * Ambil semua panduan keselamatan, dengan filter opsional kategori dan level.
     */
    List<PanduanKeselamatanResponse> getAllPanduan(String kategori, LevelFisik level);

    /**
     * Ambil panduan keselamatan berdasarkan ID.
     */
    PanduanKeselamatanResponse getPanduanById(Long id);

    /**
     * Admin: buat panduan keselamatan baru.
     */
    PanduanKeselamatanResponse createPanduan(PanduanKeselamatanRequest request);

    /**
     * Admin: update panduan keselamatan.
     */
    PanduanKeselamatanResponse updatePanduan(Long id, PanduanKeselamatanRequest request);

    /**
     * Admin: hapus panduan keselamatan.
     */
    void deletePanduan(Long id);
}
