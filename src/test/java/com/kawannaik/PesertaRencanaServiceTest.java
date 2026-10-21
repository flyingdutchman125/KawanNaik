package com.kawannaik;

import com.kawannaik.dto.response.PesertaRencanaResponse;
import com.kawannaik.exception.BadRequestException;
import com.kawannaik.exception.ResourceNotFoundException;
import com.kawannaik.model.Gunung;
import com.kawannaik.model.Jalur;
import com.kawannaik.model.Pengguna;
import com.kawannaik.model.PesertaRencana;
import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import com.kawannaik.model.enums.StatusPermintaan;
import com.kawannaik.model.enums.StatusRencana;
import com.kawannaik.repository.PenggunaRepository;
import com.kawannaik.repository.PesertaRencanaRepository;
import com.kawannaik.repository.RencanaPendakianRepository;
import com.kawannaik.service.impl.PesertaRencanaServiceImpl;
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
class PesertaRencanaServiceTest {

    @Mock
    private PesertaRencanaRepository pesertaRepository;

    @Mock
    private RencanaPendakianRepository rencanaRepository;

    @Mock
    private PenggunaRepository penggunaRepository;

    @InjectMocks
    private PesertaRencanaServiceImpl pesertaService;

    private Pengguna host;
    private Pengguna pendaki;
    private RencanaPendakian rencana;

    @BeforeEach
    void setUp() {
        host = Pengguna.builder()
                .id(1L)
                .nama("Host Budi")
                .email("host@example.com")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .build();

        pendaki = Pengguna.builder()
                .id(2L)
                .nama("Pendaki Sari")
                .email("sari@example.com")
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MENENGAH)
                .build();

        Gunung semeru = Gunung.builder().id(1L).nama("Gunung Semeru").build();
        Jalur jalur = Jalur.builder().id(1L).gunung(semeru).namaJalur("Ranu Pani").levelMinimum(LevelFisik.PEMULA).build();

        rencana = RencanaPendakian.builder()
                .id(10L)
                .pembuat(host)
                .jalur(jalur)
                .tanggalNaik(LocalDate.now().plusDays(10))
                .kuota(5)
                .levelMinimum(LevelFisik.PEMULA)
                .status(StatusRencana.DIBUKA)
                .build();
    }

    @Test
    void joinRencana_Success() {
        when(penggunaRepository.findByEmail("sari@example.com")).thenReturn(Optional.of(pendaki));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.existsByRencanaIdAndPenggunaId(10L, 2L)).thenReturn(false);
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.DITERIMA)).thenReturn(1L);
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.MENUNGGU)).thenReturn(1L);

        PesertaRencana saved = PesertaRencana.builder()
                .id(100L)
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.MENUNGGU)
                .build();
        when(pesertaRepository.save(any(PesertaRencana.class))).thenReturn(saved);

        PesertaRencanaResponse response = pesertaService.joinRencana("sari@example.com", 10L);

        assertNotNull(response);
        assertEquals(StatusPermintaan.MENUNGGU, response.getStatus());
        assertEquals("Pendaki Sari", response.getNamaPengguna());
    }

    @Test
    void joinRencana_WhenStatusNotDibuka_ThrowsBadRequestException() {
        rencana.setStatus(StatusRencana.DITUTUP);
        when(penggunaRepository.findByEmail("sari@example.com")).thenReturn(Optional.of(pendaki));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.joinRencana("sari@example.com", 10L));
        assertTrue(ex.getMessage().contains("tidak sedang dibuka"));
    }

    @Test
    void joinRencana_WhenTanggalNaikInPast_ThrowsBadRequestException() {
        rencana.setTanggalNaik(LocalDate.now().minusDays(1));
        when(penggunaRepository.findByEmail("sari@example.com")).thenReturn(Optional.of(pendaki));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.joinRencana("sari@example.com", 10L));
        assertTrue(ex.getMessage().contains("sudah berlalu"));
    }

    @Test
    void joinRencana_WhenAlreadyRegistered_ThrowsBadRequestException() {
        when(penggunaRepository.findByEmail("sari@example.com")).thenReturn(Optional.of(pendaki));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.existsByRencanaIdAndPenggunaId(10L, 2L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.joinRencana("sari@example.com", 10L));
        assertTrue(ex.getMessage().contains("sudah terdaftar"));
    }

    @Test
    void joinRencana_WhenKuotaFull_ThrowsBadRequestException() {
        when(penggunaRepository.findByEmail("sari@example.com")).thenReturn(Optional.of(pendaki));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.existsByRencanaIdAndPenggunaId(10L, 2L)).thenReturn(false);
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.DITERIMA)).thenReturn(3L);
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.MENUNGGU)).thenReturn(2L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.joinRencana("sari@example.com", 10L));
        assertTrue(ex.getMessage().contains("sudah penuh"));
    }

    @Test
    void joinRencana_HostJoiningOwnPlan_ThrowsBadRequestException() {
        when(penggunaRepository.findByEmail("host@example.com")).thenReturn(Optional.of(host));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.existsByRencanaIdAndPenggunaId(10L, 1L)).thenReturn(false);
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.DITERIMA)).thenReturn(0L);
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.MENUNGGU)).thenReturn(0L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.joinRencana("host@example.com", 10L));
        assertTrue(ex.getMessage().contains("tidak bisa bergabung ke rencana pendakian yang Anda buat sendiri"));
    }

    @Test
    void leaveRencana_WhenDepartingSoonAndAccepted_ThrowsBadRequestException() {
        rencana.setTanggalNaik(LocalDate.now().plusDays(1));
        PesertaRencana peserta = PesertaRencana.builder()
                .id(50L)
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.DITERIMA)
                .build();

        when(penggunaRepository.findByEmail("sari@example.com")).thenReturn(Optional.of(pendaki));
        when(pesertaRepository.findByRencanaIdAndPenggunaId(10L, 2L)).thenReturn(Optional.of(peserta));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.leaveRencana("sari@example.com", 10L));
        assertTrue(ex.getMessage().contains("menjelang tanggal pendakian"));
    }

    @Test
    void updateStatusPeserta_WhenNotHost_ThrowsBadRequestException() {
        Pengguna stranger = Pengguna.builder().id(99L).email("stranger@example.com").build();
        when(penggunaRepository.findByEmail("stranger@example.com")).thenReturn(Optional.of(stranger));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.updateStatusPeserta("stranger@example.com", 10L, 1L, StatusPermintaan.DITERIMA));
        assertTrue(ex.getMessage().contains("Hanya pembuat rencana"));
    }

    @Test
    void updateStatusPeserta_WhenStatusAlreadyDecided_ThrowsBadRequestException() {
        PesertaRencana peserta = PesertaRencana.builder()
                .id(1L)
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.DITERIMA)
                .build();

        when(penggunaRepository.findByEmail("host@example.com")).thenReturn(Optional.of(host));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.findById(1L)).thenReturn(Optional.of(peserta));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.updateStatusPeserta("host@example.com", 10L, 1L, StatusPermintaan.DITOLAK));
        assertTrue(ex.getMessage().contains("tidak bisa diubah lagi"));
    }

    @Test
    void updateStatusPeserta_WhenAcceptingAndQuotaFull_ThrowsBadRequestException() {
        PesertaRencana peserta = PesertaRencana.builder()
                .id(1L)
                .rencana(rencana)
                .pengguna(pendaki)
                .status(StatusPermintaan.MENUNGGU)
                .build();

        when(penggunaRepository.findByEmail("host@example.com")).thenReturn(Optional.of(host));
        when(rencanaRepository.findById(10L)).thenReturn(Optional.of(rencana));
        when(pesertaRepository.findById(1L)).thenReturn(Optional.of(peserta));
        when(pesertaRepository.countByRencanaIdAndStatus(10L, StatusPermintaan.DITERIMA)).thenReturn(5L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pesertaService.updateStatusPeserta("host@example.com", 10L, 1L, StatusPermintaan.DITERIMA));
        assertTrue(ex.getMessage().contains("sudah penuh"));
    }
}
