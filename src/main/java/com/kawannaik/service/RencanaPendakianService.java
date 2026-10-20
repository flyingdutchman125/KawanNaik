package com.kawannaik.service;

import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.response.RencanaPendakianResponse;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusRencana;

import java.time.LocalDate;
import java.util.List;

public interface RencanaPendakianService {
    RencanaPendakianResponse createRencana(String userEmail, RencanaPendakianRequest request);
    RencanaPendakianResponse getRencanaById(Long id);
    List<RencanaPendakianResponse> getAllRencana(Long gunungId, LevelFisik level, LocalDate tanggal);
    List<RencanaPendakianResponse> getMyRencana(String userEmail);

    /**
     * Update status rencana (lifecycle): DIBUKA → DITUTUP → SELESAI / DIBATALKAN.
     * Hanya pembuat rencana yang boleh mengubah status.
     */
    RencanaPendakianResponse updateStatusRencana(String userEmail, Long rencanaId, StatusRencana newStatus);

    /**
     * Pembatalan rencana oleh pemilik/host.
     * Shortcut untuk mengubah status menjadi DIBATALKAN.
     */
    RencanaPendakianResponse cancelRencana(String userEmail, Long rencanaId);
}
