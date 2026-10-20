package com.kawannaik;

import com.kawannaik.dto.request.GunungRequest;
import com.kawannaik.dto.response.GunungResponse;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Gunung;
import com.kawannaik.repository.GunungRepository;
import com.kawannaik.service.impl.GunungServiceImpl;
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
class GunungServiceTest {

    @Mock
    private GunungRepository gunungRepository;

    @InjectMocks
    private GunungServiceImpl gunungService;

    private Gunung gunung;
    private GunungRequest gunungRequest;

    @BeforeEach
    void setUp() {
        gunung = Gunung.builder()
                .id(1L)
                .nama("Gunung Semeru")
                .lokasi("Lumajang, Jawa Timur")
                .ketinggianMdpl(3676)
                .latitude(-8.108)
                .longitude(112.922)
                .kontakBasecamp("0811223344")
                .build();

        gunungRequest = GunungRequest.builder()
                .nama("Gunung Semeru")
                .lokasi("Lumajang, Jawa Timur")
                .ketinggianMdpl(3676)
                .latitude(-8.108)
                .longitude(112.922)
                .kontakBasecamp("0811223344")
                .build();
    }

    @Test
    void getAllGunung_Success() {
        when(gunungRepository.findAll()).thenReturn(Collections.singletonList(gunung));

        List<GunungResponse> list = gunungService.getAllGunung(null);

        assertEquals(1, list.size());
        assertEquals("Gunung Semeru", list.get(0).getNama());
    }

    @Test
    void getGunungById_Success() {
        when(gunungRepository.findById(1L)).thenReturn(Optional.of(gunung));

        GunungResponse response = gunungService.getGunungById(1L);

        assertNotNull(response);
        assertEquals("Gunung Semeru", response.getNama());
    }

    @Test
    void getGunungById_NotFound_ThrowsException() {
        when(gunungRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gunungService.getGunungById(999L));
    }

    @Test
    void createGunung_Success() {
        when(gunungRepository.save(any(Gunung.class))).thenReturn(gunung);

        GunungResponse response = gunungService.createGunung(gunungRequest);

        assertNotNull(response);
        assertEquals("Gunung Semeru", response.getNama());
        verify(gunungRepository, times(1)).save(any(Gunung.class));
    }
}
