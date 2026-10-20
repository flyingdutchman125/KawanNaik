package com.kawannaik.service.impl;

import com.kawannaik.dto.request.GunungRequest;
import com.kawannaik.dto.response.GunungResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Gunung;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.service.GunungService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GunungServiceImpl implements GunungService {

    private final GunungRepository gunungRepository;

    public GunungServiceImpl(GunungRepository gunungRepository) {
        this.gunungRepository = gunungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GunungResponse> getAllGunung(String search) {
        List<Gunung> list;
        if (StringUtils.hasText(search)) {
            list = gunungRepository.findByNamaContainingIgnoreCase(search);
        } else {
            list = gunungRepository.findAll();
        }

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GunungResponse getGunungById(Long id) {
        Gunung gunung = gunungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gunung dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(gunung);
    }

    @Override
    @Transactional
    public GunungResponse createGunung(GunungRequest request) {
        Gunung gunung = Gunung.builder()
                .nama(request.getNama())
                .lokasi(request.getLokasi())
                .ketinggianMdpl(request.getKetinggianMdpl())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .kontakBasecamp(request.getKontakBasecamp())
                .build();

        return mapToResponse(gunungRepository.save(gunung));
    }

    @Override
    @Transactional
    public GunungResponse updateGunung(Long id, GunungRequest request) {
        Gunung gunung = gunungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gunung dengan ID " + id + " tidak ditemukan"));

        gunung.setNama(request.getNama());
        gunung.setLokasi(request.getLokasi());
        gunung.setKetinggianMdpl(request.getKetinggianMdpl());
        gunung.setLatitude(request.getLatitude());
        gunung.setLongitude(request.getLongitude());
        gunung.setKontakBasecamp(request.getKontakBasecamp());

        return mapToResponse(gunungRepository.save(gunung));
    }

    @Override
    @Transactional
    public void deleteGunung(Long id) {
        if (!gunungRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gunung dengan ID " + id + " tidak ditemukan");
        }
        gunungRepository.deleteById(id);
    }

    private GunungResponse mapToResponse(Gunung g) {
        return GunungResponse.builder()
                .id(g.getId())
                .nama(g.getNama())
                .lokasi(g.getLokasi())
                .ketinggianMdpl(g.getKetinggianMdpl())
                .latitude(g.getLatitude())
                .longitude(g.getLongitude())
                .kontakBasecamp(g.getKontakBasecamp())
                .jumlahJalur(g.getJalurList() != null ? g.getJalurList().size() : 0)
                .createdAt(g.getCreatedAt())
                .build();
    }
}
