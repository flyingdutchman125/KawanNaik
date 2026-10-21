package com.kawannaik;

import com.kawannaik.dto.request.JalurRequest;
import com.kawannaik.dto.response.JalurResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.service.impl.JalurServiceImpl;
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
class JalurServiceTest {

    @Mock
    private JalurRepository jalurRepository;

    @Mock
    private GunungRepository gunungRepository;

    @InjectMocks
    private JalurServiceImpl jalurService;

    private Gunung gunung;
    private Jalur jalur;
    private JalurRequest jalurRequest;

    @BeforeEach
    void setUp() {
        gunung = Gunung.builder()
                .id(1L)
                .nama("Gunung Rinjani")
                .lokasi("Lombok")
                .ketinggianMdpl(3726)
                .build();

        jalur = Jalur.builder()
                .id(10L)
                .gunung(gunung)
                .namaJalur("Sembalun")
                .jarakKm(12.5)
                .tingkatKesulitan("SEDANG")
                .levelMinimum(LevelFisik.MENENGAH)
                .estimasiJam(8.0)
                .build();

        jalurRequest = JalurRequest.builder()
                .gunungId(1L)
                .namaJalur("Sembalun")
                .jarakKm(12.5)
                .tingkatKesulitan("SEDANG")
                .levelMinimum(LevelFisik.MENENGAH)
                .estimasiJam(8.0)
                .build();
    }

    @Test
    void getJalurByGunungId_Success() {
        when(jalurRepository.findByGunungId(1L)).thenReturn(Collections.singletonList(jalur));

        List<JalurResponse> result = jalurService.getJalurByGunungId(1L);

        assertEquals(1, result.size());
        assertEquals("Sembalun", result.get(0).getNamaJalur());
    }

    @Test
    void getJalurById_Success() {
        when(jalurRepository.findById(10L)).thenReturn(Optional.of(jalur));

        JalurResponse response = jalurService.getJalurById(10L);

        assertNotNull(response);
        assertEquals("Sembalun", response.getNamaJalur());
        assertEquals(1L, response.getGunungId());
    }

    @Test
    void getJalurById_NotFound_ThrowsException() {
        when(jalurRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jalurService.getJalurById(999L));
    }

    @Test
    void createJalur_Success() {
        when(gunungRepository.findById(1L)).thenReturn(Optional.of(gunung));
        when(jalurRepository.save(any(Jalur.class))).thenReturn(jalur);

        JalurResponse response = jalurService.createJalur(jalurRequest);

        assertNotNull(response);
        assertEquals("Sembalun", response.getNamaJalur());
        verify(jalurRepository, times(1)).save(any(Jalur.class));
    }

    @Test
    void createJalur_GunungNotFound_ThrowsException() {
        when(gunungRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jalurService.createJalur(jalurRequest));
        verify(jalurRepository, never()).save(any());
    }

    @Test
    void updateJalur_Success() {
        when(jalurRepository.findById(10L)).thenReturn(Optional.of(jalur));
        when(jalurRepository.save(any(Jalur.class))).thenReturn(jalur);

        jalurRequest.setNamaJalur("Sembalun Baru");
        JalurResponse response = jalurService.updateJalur(10L, jalurRequest);

        assertNotNull(response);
        verify(jalurRepository, times(1)).save(any(Jalur.class));
    }

    @Test
    void updateJalur_JalurNotFound_ThrowsException() {
        when(jalurRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jalurService.updateJalur(999L, jalurRequest));
    }

    @Test
    void updateJalur_GunungNotFound_ThrowsException() {
        when(jalurRepository.findById(10L)).thenReturn(Optional.of(jalur));
        jalurRequest.setGunungId(2L);
        when(gunungRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jalurService.updateJalur(10L, jalurRequest));
    }

    @Test
    void deleteJalur_Success() {
        when(jalurRepository.existsById(10L)).thenReturn(true);

        jalurService.deleteJalur(10L);

        verify(jalurRepository, times(1)).deleteById(10L);
    }

    @Test
    void deleteJalur_NotFound_ThrowsException() {
        when(jalurRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> jalurService.deleteJalur(999L));
        verify(jalurRepository, never()).deleteById(anyLong());
    }
}
