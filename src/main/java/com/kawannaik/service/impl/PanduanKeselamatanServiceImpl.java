package com.kawannaik.service.impl;

import com.kawannaik.dto.request.PanduanKeselamatanRequest;
import com.kawannaik.dto.response.PanduanKeselamatanResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.PanduanKeselamatan;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.repository.PanduanKeselamatanRepository;
import com.kawannaik.service.PanduanKeselamatanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PanduanKeselamatanServiceImpl implements PanduanKeselamatanService {

    private final PanduanKeselamatanRepository panduanRepository;

    public PanduanKeselamatanServiceImpl(PanduanKeselamatanRepository panduanRepository) {
        this.panduanRepository = panduanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PanduanKeselamatanResponse> getAllPanduan(String kategori, LevelFisik level) {
        List<PanduanKeselamatan> panduanList;

        if (kategori != null && level != null) {
            // Filter both kategori and level
            panduanList = panduanRepository.findByKategoriIgnoreCaseAndLevelMinimum(kategori, level);
        } else if (kategori != null) {
            panduanList = panduanRepository.findByKategoriIgnoreCase(kategori);
        } else if (level != null) {
            panduanList = panduanRepository.findByLevelMinimum(level);
        } else {
            panduanList = panduanRepository.findAll();
        }

        return panduanList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PanduanKeselamatanResponse getPanduanById(Long id) {
        PanduanKeselamatan panduan = panduanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panduan keselamatan dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(panduan);
    }

    @Override
    @Transactional
    public PanduanKeselamatanResponse createPanduan(PanduanKeselamatanRequest request) {
        PanduanKeselamatan panduan = PanduanKeselamatan.builder()
                .judul(request.getJudul())
                .isi(request.getIsi())
                .kategori(request.getKategori())
                .levelMinimum(request.getLevelMinimum())
                .build();

        return mapToResponse(panduanRepository.save(panduan));
    }

    @Override
    @Transactional
    public PanduanKeselamatanResponse updatePanduan(Long id, PanduanKeselamatanRequest request) {
        PanduanKeselamatan panduan = panduanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panduan keselamatan dengan ID " + id + " tidak ditemukan"));

        panduan.setJudul(request.getJudul());
        panduan.setIsi(request.getIsi());
        panduan.setKategori(request.getKategori());
        panduan.setLevelMinimum(request.getLevelMinimum());

        return mapToResponse(panduanRepository.save(panduan));
    }

    @Override
    @Transactional
    public void deletePanduan(Long id) {
        PanduanKeselamatan panduan = panduanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panduan keselamatan dengan ID " + id + " tidak ditemukan"));
        panduanRepository.delete(panduan);
    }

    private PanduanKeselamatanResponse mapToResponse(PanduanKeselamatan panduan) {
        return PanduanKeselamatanResponse.builder()
                .id(panduan.getId())
                .judul(panduan.getJudul())
                .isi(panduan.getIsi())
                .kategori(panduan.getKategori())
                .levelMinimum(panduan.getLevelMinimum())
                .createdAt(panduan.getCreatedAt())
                .updatedAt(panduan.getUpdatedAt())
                .build();
    }
}
