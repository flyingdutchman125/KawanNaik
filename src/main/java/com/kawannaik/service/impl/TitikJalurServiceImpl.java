package com.kawannaik.service.impl;

import com.kawannaik.dto.request.TitikJalurRequest;
import com.kawannaik.dto.response.TitikJalurResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.TitikJalur;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.repository.TitikJalurRepository;
import com.kawannaik.service.TitikJalurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TitikJalurServiceImpl implements TitikJalurService {

    private final TitikJalurRepository titikJalurRepository;
    private final JalurRepository jalurRepository;

    public TitikJalurServiceImpl(TitikJalurRepository titikJalurRepository, JalurRepository jalurRepository) {
        this.titikJalurRepository = titikJalurRepository;
        this.jalurRepository = jalurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TitikJalurResponse> getTitikByJalurId(Long jalurId) {
        return titikJalurRepository.findByJalurIdOrderByUrutanAsc(jalurId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TitikJalurResponse createTitikJalur(TitikJalurRequest request) {
        Jalur jalur = jalurRepository.findById(request.getJalurId())
                .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + request.getJalurId() + " tidak ditemukan"));

        TitikJalur titik = TitikJalur.builder()
                .jalur(jalur)
                .jenis(request.getJenis())
                .nama(request.getNama())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .deskripsi(request.getDeskripsi())
                .urutan(request.getUrutan())
                .build();

        return mapToResponse(titikJalurRepository.save(titik));
    }

    @Override
    @Transactional
    public TitikJalurResponse updateTitikJalur(Long id, TitikJalurRequest request) {
        TitikJalur titik = titikJalurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Titik jalur dengan ID " + id + " tidak ditemukan"));

        if (request.getJalurId() != null && !request.getJalurId().equals(titik.getJalur().getId())) {
            Jalur jalur = jalurRepository.findById(request.getJalurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + request.getJalurId() + " tidak ditemukan"));
            titik.setJalur(jalur);
        }

        titik.setJenis(request.getJenis());
        titik.setNama(request.getNama());
        titik.setLatitude(request.getLatitude());
        titik.setLongitude(request.getLongitude());
        titik.setDeskripsi(request.getDeskripsi());
        titik.setUrutan(request.getUrutan());

        return mapToResponse(titikJalurRepository.save(titik));
    }

    @Override
    @Transactional
    public void deleteTitikJalur(Long id) {
        if (!titikJalurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Titik jalur dengan ID " + id + " tidak ditemukan");
        }
        titikJalurRepository.deleteById(id);
    }

    private TitikJalurResponse mapToResponse(TitikJalur t) {
        return TitikJalurResponse.builder()
                .id(t.getId())
                .jalurId(t.getJalur() != null ? t.getJalur().getId() : null)
                .jenis(t.getJenis())
                .nama(t.getNama())
                .latitude(t.getLatitude())
                .longitude(t.getLongitude())
                .deskripsi(t.getDeskripsi())
                .urutan(t.getUrutan())
                .build();
    }
}
