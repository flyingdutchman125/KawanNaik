package com.kawannaik;

import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.response.RencanaPendakianResponse;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import com.kawannaik.model.enums.StatusRencana;
import com.kawannaik.repository.JalurRepository;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import com.kawannaik.service.impl.RencanaPendakianServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RencanaPendakianServiceTest {

    @Mock
    private RencanaPendakianRepository rencanaRepository;

    @Mock
    private PenggunaRepository penggunaRepository;

    @Mock
    private JalurRepository jalurRepository;

    @Mock
    private PesertaRencanaRepository pesertaRepository;

    @InjectMocks
    private RencanaPendakianServiceImpl rencanaService;

    private Pengguna pengguna;
    private Gunung gunung;
    private Jalur jalur;
    private RencanaPendakian rencana;

    @BeforeEach
    void setUp() {
        pengguna = Pengguna.builder()
                .id(1L)
                .nama("Budi")
                .email("budi@example.com")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .build();

        gunung = Gunung.builder()
                .id(1L)
                .nama("Gunung Semeru")
                .build();

        jalur = Jalur.builder()
                .id(1L)
                .gunung(gunung)
                .namaJalur("Ranu Pani")
                .levelMinimum(LevelFisik.MAHIR)
                .build();

        rencana = RencanaPendakian.builder()
                .id(1L)
                .pembuat(pengguna)
                .jalur(jalur)
                .tanggalNaik(LocalDate.now().plusDays(10))
                .kuota(5)
                .levelMinimum(LevelFisik.MAHIR)
                .status(StatusRencana.DIBUKA)
                .build();
    }

    @Test
    void createRencana_Success() {
        RencanaPendakianRequest request = RencanaPendakianRequest.builder()
                .jalurId(1L)
                .tanggalNaik(LocalDate.now().plusDays(10))
                .kuota(5)
                .levelMinimum(LevelFisik.MAHIR)
                .catatan("Kumpul di Malang")
                .build();

        when(penggunaRepository.findByEmail("budi@example.com")).thenReturn(Optional.of(pengguna));
        when(jalurRepository.findById(1L)).thenReturn(Optional.of(jalur));
        when(rencanaRepository.save(any(RencanaPendakian.class))).thenReturn(rencana);
        when(pesertaRepository.countByRencanaIdAndStatus(any(), any())).thenReturn(0L);

        RencanaPendakianResponse response = rencanaService.createRencana("budi@example.com", request);

        assertNotNull(response);
        assertEquals("Ranu Pani", response.getNamaJalur());
        assertEquals("Gunung Semeru", response.getNamaGunung());
        assertEquals("Budi", response.getNamaPembuat());
        assertEquals(5, response.getKuota());
    }

    @Test
    void getRencanaById_Success() {
        when(rencanaRepository.findById(1L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.countByRencanaIdAndStatus(any(), any())).thenReturn(2L);

        RencanaPendakianResponse response = rencanaService.getRencanaById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getJumlahPesertaDiterima());
    }
}
