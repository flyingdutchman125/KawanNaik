package com.kawannaik.service.impl;

import com.kawannaik.dto.request.JalurRequest;
import com.kawannaik.dto.response.JalurResponse;
import com.kawannaik.dto.response.TitikJalurResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.service.JalurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JalurServiceImpl implements JalurService {

    private final JalurRepository jalurRepository;
    private final GunungRepository gunungRepository;

    public JalurServiceImpl(JalurRepository jalurRepository, GunungRepository gunungRepository) {
        this.jalurRepository = jalurRepository;
        this.gunungRepository = gunungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JalurResponse> getJalurByGunungId(Long gunungId) {
        return jalurRepository.findByGunungId(gunungId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JalurResponse getJalurById(Long id) {
        Jalur jalur = jalurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + id + " tidak ditemukan"));
        return mapToResponse(jalur);
    }

    @Override
    @Transactional
    public JalurResponse createJalur(JalurRequest request) {
        Gunung gunung = gunungRepository.findById(request.getGunungId())
                .orElseThrow(() -> new ResourceNotFoundException("Gunung dengan ID " + request.getGunungId() + " tidak ditemukan"));

        Jalur jalur = Jalur.builder()
                .gunung(gunung)
                .namaJalur(request.getNamaJalur())
                .jarakKm(request.getJarakKm())
                .tingkatKesulitan(request.getTingkatKesulitan())
                .levelMinimum(request.getLevelMinimum() != null ? request.getLevelMinimum() : LevelFisik.PEMULA)
                .estimasiJam(request.getEstimasiJam())
                .build();

        return mapToResponse(jalurRepository.save(jalur));
    }

    @Override
    @Transactional
    public JalurResponse updateJalur(Long id, JalurRequest request) {
        Jalur jalur = jalurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + id + " tidak ditemukan"));

        if (request.getGunungId() != null && !request.getGunungId().equals(jalur.getGunung().getId())) {
            Gunung gunung = gunungRepository.findById(request.getGunungId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gunung dengan ID " + request.getGunungId() + " tidak ditemukan"));
            jalur.setGunung(gunung);
        }

        jalur.setNamaJalur(request.getNamaJalur());
        jalur.setJarakKm(request.getJarakKm());
        jalur.setTingkatKesulitan(request.getTingkatKesulitan());
        if (request.getLevelMinimum() != null) {
            jalur.setLevelMinimum(request.getLevelMinimum());
        }
        jalur.setEstimasiJam(request.getEstimasiJam());

        return mapToResponse(jalurRepository.save(jalur));
    }

    @Override
    @Transactional
    public void deleteJalur(Long id) {
        if (!jalurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Jalur dengan ID " + id + " tidak ditemukan");
        }
        jalurRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public String getGpxByJalurId(Long id) {
        Jalur jalur = jalurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + id + " tidak ditemukan"));
        if (jalur.getGpxData() == null || jalur.getGpxData().isBlank()) {
            throw new ResourceNotFoundException("File GPX untuk jalur ini belum tersedia");
        }
        return jalur.getGpxData();
    }

    private JalurResponse mapToResponse(Jalur j) {
        List<TitikJalurResponse> titikList = null;
        if (j.getTitikJalurList() != null) {
            titikList = j.getTitikJalurList().stream()
                    .map(t -> TitikJalurResponse.builder()
                            .id(t.getId())
                            .jalurId(j.getId())
                            .jenis(t.getJenis())
                            .nama(t.getNama())
                            .latitude(t.getLatitude())
                            .longitude(t.getLongitude())
                            .deskripsi(t.getDeskripsi())
                            .urutan(t.getUrutan())
                            .build())
                    .collect(Collectors.toList());
        }

        List<List<Double>> gpxTrack = parseGpxTrack(j.getGpxData());
        boolean hasGpx = j.getGpxData() != null && !j.getGpxData().isBlank();

        return JalurResponse.builder()
                .id(j.getId())
                .gunungId(j.getGunung() != null ? j.getGunung().getId() : null)
                .namaGunung(j.getGunung() != null ? j.getGunung().getNama() : null)
                .namaJalur(j.getNamaJalur())
                .jarakKm(j.getJarakKm())
                .tingkatKesulitan(j.getTingkatKesulitan())
                .levelMinimum(j.getLevelMinimum())
                .estimasiJam(j.getEstimasiJam())
                .titikJalurList(titikList)
                .gpxTrack(gpxTrack)
                .hasGpx(hasGpx)
                .build();
    }

    private List<List<Double>> parseGpxTrack(String gpxXml) {
        if (gpxXml == null || gpxXml.isBlank()) {
            return java.util.Collections.emptyList();
        }
        List<List<Double>> track = new java.util.ArrayList<>();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("<trkpt[^>]+lat=[\"']([^\"']+)[\"'][^>]+lon=[\"']([^\"']+)[\"']").matcher(gpxXml);
        while (matcher.find()) {
            try {
                double lat = Double.parseDouble(matcher.group(1));
                double lon = Double.parseDouble(matcher.group(2));
                track.add(List.of(lat, lon));
            } catch (NumberFormatException ignored) {}
        }
        if (track.isEmpty()) {
            java.util.regex.Matcher matcher2 = java.util.regex.Pattern.compile("<trkpt[^>]+lon=[\"']([^\"']+)[\"'][^>]+lat=[\"']([^\"']+)[\"']").matcher(gpxXml);
            while (matcher2.find()) {
                try {
                    double lon = Double.parseDouble(matcher2.group(1));
                    double lat = Double.parseDouble(matcher2.group(2));
                    track.add(List.of(lat, lon));
                } catch (NumberFormatException ignored) {}
            }
        }
        return track;
    }
}
