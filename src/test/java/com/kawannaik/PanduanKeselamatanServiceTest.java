package com.kawannaik;

import com.kawannaik.dto.request.PanduanKeselamatanRequest;
import com.kawannaik.dto.response.PanduanKeselamatanResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.PanduanKeselamatan;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.repository.PanduanKeselamatanRepository;
import com.kawannaik.service.impl.PanduanKeselamatanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanduanKeselamatanServiceTest {

    @Mock
    private PanduanKeselamatanRepository panduanRepository;

    @InjectMocks
    private PanduanKeselamatanServiceImpl panduanService;

    private PanduanKeselamatan panduan;
    private PanduanKeselamatanRequest request;

    @BeforeEach
    void setUp() {
        panduan = PanduanKeselamatan.builder()
                .id(1L)
                .judul("Pencegahan Hipotermia")
                .isi("Gunakan pakaian berlapis...")
                .kategori("MEDIS")
                .levelMinimum(LevelFisik.PEMULA)
                .build();

        request = PanduanKeselamatanRequest.builder()
                .judul("Pencegahan Hipotermia")
                .isi("Gunakan pakaian berlapis...")
                .kategori("MEDIS")
                .levelMinimum(LevelFisik.PEMULA)
                .build();
    }

    @Test
    void getAllPanduan_NoFilter() {
        when(panduanRepository.findAll()).thenReturn(Collections.singletonList(panduan));

        List<PanduanKeselamatanResponse> list = panduanService.getAllPanduan(null, null);

        assertEquals(1, list.size());
        assertEquals("Pencegahan Hipotermia", list.get(0).getJudul());
        verify(panduanRepository, times(1)).findAll();
    }

    @Test
    void getAllPanduan_FilterKategoriOnly() {
        when(panduanRepository.findByKategoriIgnoreCase("MEDIS")).thenReturn(Collections.singletonList(panduan));

        List<PanduanKeselamatanResponse> list = panduanService.getAllPanduan("MEDIS", null);

        assertEquals(1, list.size());
        verify(panduanRepository, times(1)).findByKategoriIgnoreCase("MEDIS");
    }

    @Test
    void getAllPanduan_FilterLevelOnly() {
        when(panduanRepository.findByLevelMinimum(LevelFisik.PEMULA)).thenReturn(Collections.singletonList(panduan));

        List<PanduanKeselamatanResponse> list = panduanService.getAllPanduan(null, LevelFisik.PEMULA);

        assertEquals(1, list.size());
        verify(panduanRepository, times(1)).findByLevelMinimum(LevelFisik.PEMULA);
    }

    @Test
    void getAllPanduan_FilterBoth() {
        when(panduanRepository.findByKategoriIgnoreCaseAndLevelMinimum("MEDIS", LevelFisik.PEMULA))
                .thenReturn(Collections.singletonList(panduan));

        List<PanduanKeselamatanResponse> list = panduanService.getAllPanduan("MEDIS", LevelFisik.PEMULA);

        assertEquals(1, list.size());
        verify(panduanRepository, times(1)).findByKategoriIgnoreCaseAndLevelMinimum("MEDIS", LevelFisik.PEMULA);
    }

    @Test
    void getPanduanById_Success() {
        when(panduanRepository.findById(1L)).thenReturn(Optional.of(panduan));

        PanduanKeselamatanResponse response = panduanService.getPanduanById(1L);

        assertNotNull(response);
        assertEquals("Pencegahan Hipotermia", response.getJudul());
    }

    @Test
    void getPanduanById_NotFound_ThrowsException() {
        when(panduanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> panduanService.getPanduanById(999L));
    }

    @Test
    void createPanduan_Success() {
        when(panduanRepository.save(any(PanduanKeselamatan.class))).thenReturn(panduan);

        PanduanKeselamatanResponse response = panduanService.createPanduan(request);

        assertNotNull(response);
        assertEquals("Pencegahan Hipotermia", response.getJudul());
        verify(panduanRepository, times(1)).save(any(PanduanKeselamatan.class));
    }

    @Test
    void updatePanduan_Success() {
        when(panduanRepository.findById(1L)).thenReturn(Optional.of(panduan));
        when(panduanRepository.save(any(PanduanKeselamatan.class))).thenReturn(panduan);

        request.setJudul("Pencegahan Hipotermia Updated");
        PanduanKeselamatanResponse response = panduanService.updatePanduan(1L, request);

        assertNotNull(response);
        verify(panduanRepository, times(1)).save(any(PanduanKeselamatan.class));
    }

    @Test
    void updatePanduan_NotFound_ThrowsException() {
        when(panduanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> panduanService.updatePanduan(999L, request));
    }

    @Test
    void deletePanduan_Success() {
        when(panduanRepository.findById(1L)).thenReturn(Optional.of(panduan));

        panduanService.deletePanduan(1L);

        verify(panduanRepository, times(1)).delete(panduan);
    }

    @Test
    void deletePanduan_NotFound_ThrowsException() {
        when(panduanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> panduanService.deletePanduan(999L));
    }
}
