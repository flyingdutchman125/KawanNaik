package com.kawannaik.service;

import com.kawannaik.dto.response.PesertaRencanaResponse;
import com.kawannaik.model.enums.StatusPermintaan;

import java.util.List;

public interface PesertaRencanaService {

    /**
     * Ajukan bergabung ke rencana pendakian.
     * Melakukan validasi: rencana harus DIBUKA, kuota belum penuh,
     * belum pernah mendaftar sebelumnya, tanggal naik belum lewat,
     * dan menentukan StatusKelayakan otomatis.
     */
    PesertaRencanaResponse joinRencana(String userEmail, Long rencanaId);

    /**
     * Batalkan ajuan atau tinggalkan rencana pendakian.
     */
    void leaveRencana(String userEmail, Long rencanaId);

    /**
     * Host menerima atau menolak peserta.
     * Hanya pembuat rencana yang boleh mengubah status peserta.
     */
    PesertaRencanaResponse updateStatusPeserta(String userEmail, Long rencanaId, Long pesertaId, StatusPermintaan statusPermintaan);

    /**
     * Daftar peserta per rencana pendakian.
     */
    List<PesertaRencanaResponse> getPesertaByRencana(Long rencanaId);
}
