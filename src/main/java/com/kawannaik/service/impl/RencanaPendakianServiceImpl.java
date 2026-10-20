package com.kawannaik.service.impl;

import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.response.RencanaPendakianResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusPermintaan;
import com.kawannaik.model.enums.StatusRencana;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import com.kawannaik.service.RencanaPendakianService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RencanaPendakianServiceImpl implements RencanaPendakianService {

    private final RencanaPendakianRepository rencanaRepository;
    private final PenggunaRepository penggunaRepository;
    private final JalurRepository jalurRepository;
    private final PesertaRencanaRepository pesertaRepository;

    public RencanaPendakianServiceImpl(
            RencanaPendakianRepository rencanaRepository,
            PenggunaRepository penggunaRepository,
            JalurRepository jalurRepository,
            PesertaRencanaRepository pesertaRepository) {
        this.rencanaRepository = rencanaRepository;
        this.penggunaRepository = penggunaRepository;
        this.jalurRepository = jalurRepository;
        this.pesertaRepository = pesertaRepository;
    }

    @Override
    @Transactional
    public RencanaPendakianResponse createRencana(String userEmail, RencanaPendakianRequest request) {
        Pengguna pembuat = penggunaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));

        Jalur jalur = jalurRepository.findById(request.getJalurId())
                .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + request.getJalurId() + " tidak ditemukan"));

        RencanaPendakian rencana = RencanaPendakian.builder()
                .pembuat(pembuat)
                .jalur(jalur)
                .tanggalNaik(request.getTanggalNaik())
                .kuota(request.getKuota())
                .levelMinimum(request.getLevelMinimum() != null ? request.getLevelMinimum() : jalur.getLevelMinimum())
                .catatan(request.getCatatan())
                .status(StatusRencana.DIBUKA)
                .build();

        return mapToResponse(rencanaRepository.save(rencana));
    }

    @Override
    @Transactional(readOnly = true)
    public RencanaPendakianResponse getRencanaById(Long id) {
        RencanaPendakian rencana = rencanaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rencana pendakian dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(rencana);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RencanaPendakianResponse> getAllRencana(Long gunungId, LevelFisik level, LocalDate tanggal) {
        return rencanaRepository.searchRencana(gunungId, level, tanggal).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RencanaPendakianResponse> getMyRencana(String userEmail) {
        Pengguna pengguna = penggunaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));

        return rencanaRepository.findByPembuatId(pengguna.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RencanaPendakianResponse mapToResponse(RencanaPendakian r) {
        long diterimaCount = pesertaRepository.countByRencanaIdAndStatus(r.getId(), StatusPermintaan.DITERIMA);

        return RencanaPendakianResponse.builder()
                .id(r.getId())
                .pembuatId(r.getPembuat() != null ? r.getPembuat().getId() : null)
                .namaPembuat(r.getPembuat() != null ? r.getPembuat().getNama() : null)
                .levelPembuat(r.getPembuat() != null ? r.getPembuat().getLevelFisik() : null)
                .jalurId(r.getJalur() != null ? r.getJalur().getId() : null)
                .namaJalur(r.getJalur() != null ? r.getJalur().getNamaJalur() : null)
                .namaGunung(r.getJalur() != null && r.getJalur().getGunung() != null ? r.getJalur().getGunung().getNama() : null)
                .tanggalNaik(r.getTanggalNaik())
                .kuota(r.getKuota())
                .jumlahPesertaDiterima(diterimaCount)
                .levelMinimum(r.getLevelMinimum())
                .statusKelayakan(r.getStatusKelayakan())
                .catatan(r.getCatatan())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
