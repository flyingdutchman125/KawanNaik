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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
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

    @Override
    @Transactional
    public int importGpx(Long jalurId, String gpxContent) {
        if (gpxContent == null || gpxContent.length() > 10_000_000) {
            throw new IllegalArgumentException("File GPX kosong atau terlalu besar");
        }
        Jalur jalur = jalurRepository.findById(jalurId)
                .orElseThrow(() -> new ResourceNotFoundException("Jalur dengan ID " + jalurId + " tidak ditemukan"));
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            NodeList nodes = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(gpxContent)))
                    .getElementsByTagName("trkpt");
            if (nodes.getLength() < 2) throw new IllegalArgumentException("GPX harus memiliki minimal 2 titik track");

            titikJalurRepository.deleteByJalurId(jalurId);
            for (int i = 0; i < nodes.getLength(); i++) {
                var node = (org.w3c.dom.Element) nodes.item(i);
                String lat = node.getAttribute("lat");
                String lon = node.getAttribute("lon");
                if (lat.isBlank() || lon.isBlank()) continue;
                com.kawannaik.model.enums.JenisTitik jenis = i == 0
                        ? com.kawannaik.model.enums.JenisTitik.BASECAMP
                        : i == nodes.getLength() - 1
                        ? com.kawannaik.model.enums.JenisTitik.PUNCAK
                        : com.kawannaik.model.enums.JenisTitik.POS;
                TitikJalur titik = TitikJalur.builder()
                        .jalur(jalur)
                        .jenis(jenis)
                        .nama(i == 0 ? "Awal jalur" : i == nodes.getLength() - 1 ? "Puncak" : "Titik " + (i + 1))
                        .latitude(Double.valueOf(lat))
                        .longitude(Double.valueOf(lon))
                        .urutan(i + 1)
                        .build();
                titikJalurRepository.save(titik);
            }
            return nodes.getLength();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Format GPX tidak valid", e);
        }
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
