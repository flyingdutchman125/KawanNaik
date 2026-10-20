package com.kawannaik.service.impl;

import com.kawannaik.dto.response.PesertaRencanaResponse;
import com.kawannaik.exception.BadRequestException;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.PesertaRencana;
import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusKelayakan;
import com.kawannaik.model.enums.StatusPermintaan;
import com.kawannaik.model.enums.StatusRencana;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import com.kawannaik.service.PesertaRencanaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PesertaRencanaServiceImpl implements PesertaRencanaService {

    private final PesertaRencanaRepository pesertaRepository;
    private final RencanaPendakianRepository rencanaRepository;
    private final PenggunaRepository penggunaRepository;

    public PesertaRencanaServiceImpl(
            PesertaRencanaRepository pesertaRepository,
            RencanaPendakianRepository rencanaRepository,
            PenggunaRepository penggunaRepository) {
        this.pesertaRepository = pesertaRepository;
        this.rencanaRepository = rencanaRepository;
        this.penggunaRepository = penggunaRepository;
    }

    @Override
    @Transactional
    public PesertaRencanaResponse joinRencana(String userEmail, Long rencanaId) {
        Pengguna pengguna = penggunaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));

        RencanaPendakian rencana = rencanaRepository.findById(rencanaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rencana pendakian dengan ID " + rencanaId + " tidak ditemukan"));

        // Validasi: rencana harus berstatus DIBUKA
        if (rencana.getStatus() != StatusRencana.DIBUKA) {
            throw new BadRequestException("Rencana pendakian tidak sedang dibuka untuk pendaftaran");
        }

        // Validasi: tanggal naik tidak boleh di masa lalu
        if (rencana.getTanggalNaik().isBefore(LocalDate.now())) {
            throw new BadRequestException("Tanggal pendakian sudah berlalu, tidak bisa bergabung");
        }

        // Validasi: pencegahan mendaftar dua kali pada rencana yang sama
        if (pesertaRepository.existsByRencanaIdAndPenggunaId(rencanaId, pengguna.getId())) {
            throw new BadRequestException("Anda sudah terdaftar pada rencana pendakian ini");
        }

        // Validasi: pengecekan kuota maksimal peserta
        long pesertaDiterima = pesertaRepository.countByRencanaIdAndStatus(rencanaId, StatusPermintaan.DITERIMA);
        long pesertaMenunggu = pesertaRepository.countByRencanaIdAndStatus(rencanaId, StatusPermintaan.MENUNGGU);
        if (pesertaDiterima + pesertaMenunggu >= rencana.getKuota()) {
            throw new BadRequestException("Kuota pendaftaran rencana pendakian ini sudah penuh");
        }

        // Validasi: pembuat tidak bisa join rencana sendiri
        if (rencana.getPembuat().getId().equals(pengguna.getId())) {
            throw new BadRequestException("Anda tidak bisa bergabung ke rencana pendakian yang Anda buat sendiri");
        }

        // Logika penentuan Status Kelayakan otomatis
        StatusKelayakan kelayakan = determineKelayakan(pengguna.getLevelFisik(), rencana.getLevelMinimum());

        PesertaRencana peserta = PesertaRencana.builder()
                .rencana(rencana)
                .pengguna(pengguna)
                .status(StatusPermintaan.MENUNGGU)
                .build();

        PesertaRencana saved = pesertaRepository.save(peserta);
        return mapToResponse(saved, kelayakan);
    }

    @Override
    @Transactional
    public void leaveRencana(String userEmail, Long rencanaId) {
        Pengguna pengguna = penggunaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));

        PesertaRencana peserta = pesertaRepository.findByRencanaIdAndPenggunaId(rencanaId, pengguna.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Anda tidak terdaftar pada rencana pendakian ini"));

        // Tidak bisa meninggalkan rencana jika sudah DITERIMA dan tanggal naik hari ini atau besok
        RencanaPendakian rencana = peserta.getRencana();
        if (peserta.getStatus() == StatusPermintaan.DITERIMA
                && rencana.getTanggalNaik() != null
                && !rencana.getTanggalNaik().isAfter(LocalDate.now().plusDays(1))) {
            throw new BadRequestException("Tidak bisa meninggalkan rencana yang sudah diterima menjelang tanggal pendakian");
        }

        pesertaRepository.delete(peserta);
    }

    @Override
    @Transactional
    public PesertaRencanaResponse updateStatusPeserta(String userEmail, Long rencanaId, Long pesertaId, StatusPermintaan statusPermintaan) {
        Pengguna host = penggunaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));

        RencanaPendakian rencana = rencanaRepository.findById(rencanaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rencana pendakian dengan ID " + rencanaId + " tidak ditemukan"));

        // Validasi: hanya pembuat rencana (host) yang boleh mengubah status peserta
        if (!rencana.getPembuat().getId().equals(host.getId())) {
            throw new BadRequestException("Hanya pembuat rencana yang bisa menerima atau menolak peserta");
        }

        PesertaRencana peserta = pesertaRepository.findById(pesertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Peserta dengan ID " + pesertaId + " tidak ditemukan"));

        // Validasi: peserta harus milik rencana ini
        if (!peserta.getRencana().getId().equals(rencanaId)) {
            throw new BadRequestException("Peserta tidak terdaftar pada rencana pendakian ini");
        }

        // Validasi: hanya bisa mengubah status peserta yang masih MENUNGGU
        if (peserta.getStatus() != StatusPermintaan.MENUNGGU) {
            throw new BadRequestException("Status peserta sudah " + peserta.getStatus() + ", tidak bisa diubah lagi");
        }

        // Validasi: jika menerima, pastikan kuota tidak terlampaui
        if (statusPermintaan == StatusPermintaan.DITERIMA) {
            long pesertaDiterima = pesertaRepository.countByRencanaIdAndStatus(rencanaId, StatusPermintaan.DITERIMA);
            if (pesertaDiterima >= rencana.getKuota()) {
                throw new BadRequestException("Kuota peserta yang diterima sudah penuh");
            }
        }

        // Validasi: status yang diberikan harus DITERIMA atau DITOLAK
        if (statusPermintaan == StatusPermintaan.MENUNGGU) {
            throw new BadRequestException("Status permintaan harus DITERIMA atau DITOLAK");
        }

        peserta.setStatus(statusPermintaan);
        PesertaRencana updated = pesertaRepository.save(peserta);

        StatusKelayakan kelayakan = determineKelayakan(
                peserta.getPengguna().getLevelFisik(),
                rencana.getLevelMinimum());

        return mapToResponse(updated, kelayakan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PesertaRencanaResponse> getPesertaByRencana(Long rencanaId) {
        // Validasi rencana ada
        RencanaPendakian rencana = rencanaRepository.findById(rencanaId)
                .orElseThrow(() -> new ResourceNotFoundException("Rencana pendakian dengan ID " + rencanaId + " tidak ditemukan"));

        return pesertaRepository.findByRencanaId(rencanaId).stream()
                .map(peserta -> {
                    StatusKelayakan kelayakan = determineKelayakan(
                            peserta.getPengguna().getLevelFisik(),
                            rencana.getLevelMinimum());
                    return mapToResponse(peserta, kelayakan);
                })
                .collect(Collectors.toList());
    }

    /**
     * Logika penentuan Status Kelayakan otomatis.
     * Membandingkan level_fisik pengguna dengan level_minimum jalur/rencana.
     *
     * LevelFisik ordinal: PEMULA(0) < MENENGAH(1) < MAHIR(2)
     *
     * - Jika level pengguna >= level minimum → LAYAK
     * - Jika level pengguna tepat 1 tingkat di bawah → WASPADA
     * - Jika level pengguna 2+ tingkat di bawah → TIDAK_DISARANKAN
     */
    private StatusKelayakan determineKelayakan(LevelFisik levelPengguna, LevelFisik levelMinimum) {
        if (levelPengguna == null || levelMinimum == null) {
            return StatusKelayakan.WASPADA;
        }

        int diff = levelMinimum.ordinal() - levelPengguna.ordinal();

        if (diff <= 0) {
            return StatusKelayakan.LAYAK;
        } else if (diff == 1) {
            return StatusKelayakan.WASPADA;
        } else {
            return StatusKelayakan.TIDAK_DISARANKAN;
        }
    }

    private PesertaRencanaResponse mapToResponse(PesertaRencana peserta, StatusKelayakan kelayakan) {
        Pengguna pengguna = peserta.getPengguna();

        return PesertaRencanaResponse.builder()
                .id(peserta.getId())
                .rencanaId(peserta.getRencana() != null ? peserta.getRencana().getId() : null)
                .penggunaId(pengguna != null ? pengguna.getId() : null)
                .namaPengguna(pengguna != null ? pengguna.getNama() : null)
                .emailPengguna(pengguna != null ? pengguna.getEmail() : null)
                .levelFisikPengguna(pengguna != null ? pengguna.getLevelFisik() : null)
                .status(peserta.getStatus())
                .statusKelayakan(kelayakan)
                .tanggalAjuan(peserta.getTanggalAjuan())
                .build();
    }
}
