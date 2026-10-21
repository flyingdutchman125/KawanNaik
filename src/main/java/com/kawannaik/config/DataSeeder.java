package com.kawannaik.config;

import com.kawannaik.model.*;
import com.kawannaik.model.enums.*;
import com.kawannaik.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * DataSeeder — mengimpor data awal gunung, jalur, titik pos, rencana pendakian,
 * peserta, panduan keselamatan, dan akun admin default dari schema.sql.
 * <p>
 * Hanya dijalankan jika profil "!test" aktif (tidak aktif di test).
 * Seed hanya berjalan sekali — skip jika data sudah ada.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PenggunaRepository penggunaRepository;
    private final GunungRepository gunungRepository;
    private final JalurRepository jalurRepository;
    private final TitikJalurRepository titikJalurRepository;
    private final RencanaPendakianRepository rencanaRepository;
    private final PesertaRencanaRepository pesertaRepository;
    private final PanduanKeselamatanRepository panduanRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PenggunaRepository penggunaRepository,
                      GunungRepository gunungRepository,
                      JalurRepository jalurRepository,
                      TitikJalurRepository titikJalurRepository,
                      RencanaPendakianRepository rencanaRepository,
                      PesertaRencanaRepository pesertaRepository,
                      PanduanKeselamatanRepository panduanRepository,
                      PasswordEncoder passwordEncoder) {
        this.penggunaRepository = penggunaRepository;
        this.gunungRepository = gunungRepository;
        this.jalurRepository = jalurRepository;
        this.titikJalurRepository = titikJalurRepository;
        this.rencanaRepository = rencanaRepository;
        this.pesertaRepository = pesertaRepository;
        this.panduanRepository = panduanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (penggunaRepository.count() > 0) {
            log.info("Data sudah ada — DataSeeder dilewati.");
            return;
        }

        log.info("======= DataSeeder: Memulai import data awal =======");

        // ---------------------------------------------------------------
        // 1. Pengguna (password = 'password123')
        // ---------------------------------------------------------------
        String encodedPassword = passwordEncoder.encode("password123");

        Pengguna admin = penggunaRepository.save(Pengguna.builder()
                .nama("Admin KawanNaik")
                .email("admin@kawannaik.com")
                .passwordHash(encodedPassword)
                .peran(Peran.ADMIN)
                .levelFisik(LevelFisik.MAHIR)
                .pengalaman("Berpengalaman mendaki sejak 2010")
                .kontakDarurat("08001234567")
                .build());

        Pengguna budi = penggunaRepository.save(Pengguna.builder()
                .nama("Budi Santoso")
                .email("budi@example.com")
                .passwordHash(encodedPassword)
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .pengalaman("Sudah mendaki 20+ gunung di Jawa dan Sumatera")
                .kontakDarurat("08111111111")
                .build());

        Pengguna sari = penggunaRepository.save(Pengguna.builder()
                .nama("Sari Wulandari")
                .email("sari@example.com")
                .passwordHash(encodedPassword)
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MENENGAH)
                .pengalaman("Hobi hiking sejak kuliah, 10 gunung")
                .kontakDarurat("08122222222")
                .build());

        Pengguna andi = penggunaRepository.save(Pengguna.builder()
                .nama("Andi Pratama")
                .email("andi@example.com")
                .passwordHash(encodedPassword)
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.PEMULA)
                .pengalaman("Baru mulai mendaki, antusias belajar")
                .kontakDarurat("08133333333")
                .build());

        log.info("  ✔ {} pengguna disimpan (termasuk admin)", penggunaRepository.count());

        // ---------------------------------------------------------------
        // 2. Gunung (Jawa Timur)
        // ---------------------------------------------------------------
        Gunung semeru = gunungRepository.save(Gunung.builder()
                .nama("Gunung Semeru")
                .lokasi("Lumajang, Jawa Timur")
                .ketinggianMdpl(3676)
                .latitude(-8.108)
                .longitude(112.922)
                .build());

        Gunung arjuno = gunungRepository.save(Gunung.builder()
                .nama("Gunung Arjuno")
                .lokasi("Malang-Pasuruan, Jawa Timur")
                .ketinggianMdpl(3339)
                .latitude(-7.761)
                .longitude(112.596)
                .build());

        Gunung welirang = gunungRepository.save(Gunung.builder()
                .nama("Gunung Welirang")
                .lokasi("Pasuruan, Jawa Timur")
                .ketinggianMdpl(3156)
                .latitude(-7.766)
                .longitude(112.577)
                .build());

        log.info("  ✔ {} gunung disimpan", gunungRepository.count());

        // ---------------------------------------------------------------
        // 3. Jalur
        // ---------------------------------------------------------------
        Jalur ranuPani = jalurRepository.save(Jalur.builder()
                .gunung(semeru)
                .namaJalur("Ranu Pani")
                .tingkatKesulitan("Sulit")
                .levelMinimum(LevelFisik.MAHIR)
                .estimasiJam(12.0)
                .build());

        Jalur arekArek = jalurRepository.save(Jalur.builder()
                .gunung(semeru)
                .namaJalur("Arek-Arek")
                .tingkatKesulitan("Sangat Sulit")
                .levelMinimum(LevelFisik.MAHIR)
                .estimasiJam(14.0)
                .build());

        Jalur lawangTretes = jalurRepository.save(Jalur.builder()
                .gunung(arjuno)
                .namaJalur("Lawang via Tretes")
                .tingkatKesulitan("Sedang")
                .levelMinimum(LevelFisik.MENENGAH)
                .estimasiJam(8.0)
                .build());

        Jalur viaTretes = jalurRepository.save(Jalur.builder()
                .gunung(welirang)
                .namaJalur("via Tretes")
                .tingkatKesulitan("Mudah-Sedang")
                .levelMinimum(LevelFisik.PEMULA)
                .estimasiJam(6.0)
                .build());

        log.info("  ✔ {} jalur disimpan", jalurRepository.count());

        // ---------------------------------------------------------------
        // 4. Titik Jalur
        // ---------------------------------------------------------------
        // Semeru — Ranu Pani
        seedTitik(ranuPani, JenisTitik.BASECAMP, "Basecamp Ranu Pani", -8.013, 112.952,
                "Titik awal pendakian via Ranu Pani, fasilitas MCK tersedia", 1);
        seedTitik(ranuPani, JenisTitik.POS, "Pos 1 Watu Rejeng", -8.030, 112.940,
                "Pos pertama, pemandangan tebing batu", 2);
        seedTitik(ranuPani, JenisTitik.POS, "Pos 2 Kalimati", -8.065, 112.930,
                "Pos terakhir sebelum puncak, area camping luas", 3);
        seedTitik(ranuPani, JenisTitik.SUMBER_AIR, "Sumber Air Kalimati", -8.066, 112.931,
                "Sumber air terakhir sebelum summit push", 4);
        seedTitik(ranuPani, JenisTitik.TITIK_RAWAN, "Tanjakan Cinta", -8.050, 112.935,
                "Tanjakan terjal, rawan kelelahan dan tergelincir", 5);

        // Semeru — Arek-Arek
        seedTitik(arekArek, JenisTitik.BASECAMP, "Basecamp Arek-Arek", -8.010, 112.945,
                "Titik start jalur alternatif Semeru", 1);
        seedTitik(arekArek, JenisTitik.POS, "Pos Oro-Oro Ombo", -8.035, 112.938,
                "Padang savana luas, spot foto populer", 2);
        seedTitik(arekArek, JenisTitik.SUMBER_AIR, "Sumber Air Ranu Kumbolo", -8.050, 112.932,
                "Danau Ranu Kumbolo, sumber air utama", 3);
        seedTitik(arekArek, JenisTitik.TITIK_RAWAN, "Tanjakan Besi", -8.070, 112.928,
                "Tanjakan sangat curam menuju puncak Mahameru", 4);

        // Arjuno via Tretes
        seedTitik(lawangTretes, JenisTitik.BASECAMP, "Basecamp Tretes", -7.720, 112.620,
                "Basecamp utama pendakian Arjuno dari Tretes", 1);
        seedTitik(lawangTretes, JenisTitik.POS, "Pos 1 Lalijiwo", -7.740, 112.610,
                "Pos peristirahatan, vegetasi hutan montane", 2);
        seedTitik(lawangTretes, JenisTitik.SUMBER_AIR, "Sumber Air Lalijiwo", -7.742, 112.608,
                "Sumber air bersih di area Lalijiwo", 3);
        seedTitik(lawangTretes, JenisTitik.TITIK_RAWAN, "Angin Kencang Puncak", -7.761, 112.596,
                "Area puncak rawan angin kencang, waspadai cuaca buruk", 4);

        // Welirang via Tretes
        seedTitik(viaTretes, JenisTitik.BASECAMP, "Basecamp Tretes", -7.720, 112.620,
                "Titik awal pendakian Welirang via Tretes", 1);
        seedTitik(viaTretes, JenisTitik.POS, "Pos 1 Air Panas", -7.735, 112.605,
                "Pos dekat sumber air panas alami", 2);
        seedTitik(viaTretes, JenisTitik.SUMBER_AIR, "Sumber Air Air Panas", -7.736, 112.604,
                "Sumber air panas, bisa untuk berendam", 3);
        seedTitik(viaTretes, JenisTitik.TITIK_RAWAN, "Kawah Belerang", -7.766, 112.577,
                "Area kawah aktif, wajib pakai masker, hindari saat berangin", 4);

        log.info("  ✔ {} titik jalur disimpan", titikJalurRepository.count());

        // ---------------------------------------------------------------
        // 5. Rencana Pendakian
        // ---------------------------------------------------------------
        RencanaPendakian rencanaSemeru = rencanaRepository.save(RencanaPendakian.builder()
                .pembuat(budi)
                .jalur(ranuPani)
                .tanggalNaik(LocalDate.of(2026, 11, 15))
                .kuota(5)
                .levelMinimum(LevelFisik.MAHIR)
                .catatan("Pendakian Semeru via Ranu Pani. Wajib bawa sleeping bag dan jaket tebal. Kumpul di Malang H-1.")
                .status(StatusRencana.DIBUKA)
                .build());

        RencanaPendakian rencanaWelirang = rencanaRepository.save(RencanaPendakian.builder()
                .pembuat(sari)
                .jalur(viaTretes)
                .tanggalNaik(LocalDate.of(2026, 11, 22))
                .kuota(8)
                .levelMinimum(LevelFisik.PEMULA)
                .catatan("Pendakian santai Welirang, cocok untuk pemula. Peralatan dasar disediakan.")
                .status(StatusRencana.DIBUKA)
                .build());

        log.info("  ✔ {} rencana pendakian disimpan", rencanaRepository.count());

        // ---------------------------------------------------------------
        // 6. Peserta Rencana
        // ---------------------------------------------------------------
        pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencanaSemeru).pengguna(sari).status(StatusPermintaan.MENUNGGU).build());
        pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencanaSemeru).pengguna(andi).status(StatusPermintaan.DITOLAK).build());
        pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencanaWelirang).pengguna(budi).status(StatusPermintaan.DITERIMA).build());
        pesertaRepository.save(PesertaRencana.builder()
                .rencana(rencanaWelirang).pengguna(andi).status(StatusPermintaan.MENUNGGU).build());

        log.info("  ✔ {} peserta rencana disimpan", pesertaRepository.count());

        // ---------------------------------------------------------------
        // 7. Panduan Keselamatan
        // ---------------------------------------------------------------
        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Pencegahan Hipotermia")
                .isi("Hipotermia terjadi saat suhu tubuh turun di bawah 35°C. Untuk mencegahnya:\n"
                        + "1. Gunakan sistem layering: base layer, mid layer, dan outer layer.\n"
                        + "2. Hindari pakaian basah — bawa pakaian ganti dalam dry bag.\n"
                        + "3. Konsumsi makanan berkalori tinggi dan minuman hangat secara teratur.\n"
                        + "4. Segera pasang bivak atau tenda jika menggigil tak terkendali.\n"
                        + "5. Jangan mendaki sendirian — sistem buddy membantu deteksi dini gejala.")
                .kategori("Kesehatan")
                .build());

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Mengenali dan Mengatasi Altitude Sickness")
                .isi("Acute Mountain Sickness (AMS) bisa terjadi di ketinggian > 2.500 mdpl. Gejala:\n"
                        + "- Sakit kepala, mual, pusing, sulit tidur.\n"
                        + "Pencegahan:\n"
                        + "1. Aklimatisasi bertahap — jangan naikkan ketinggian tidur lebih dari 500m/hari.\n"
                        + "2. Minum air minimal 3 liter/hari.\n"
                        + "3. Hindari alkohol dan obat tidur.\n"
                        + "Penanganan: Jika gejala memberat, TURUN SEGERA minimal 500m. Jangan paksakan naik.")
                .kategori("Kesehatan")
                .build());

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Tips Navigasi di Gunung")
                .isi("Tersesat adalah penyebab utama kecelakaan pendakian. Tips navigasi:\n"
                        + "1. Pelajari rute sebelum berangkat — unduh peta offline (misalnya via Avenza Maps).\n"
                        + "2. Bawa kompas dan peta fisik sebagai cadangan.\n"
                        + "3. Perhatikan tanda jalur (pita, cat di batu, cairn) dan catat waktu di setiap pos.\n"
                        + "4. Jika tersesat: STOP — Sit, Think, Observe, Plan. Jangan panik.\n"
                        + "5. Gunakan peluit untuk sinyal darurat (3x tiup berulang).")
                .kategori("Navigasi")
                .build());

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Protokol Darurat di Gunung")
                .isi("Jika terjadi kecelakaan atau kondisi darurat:\n"
                        + "1. Pastikan keselamatan diri dan tim terlebih dahulu.\n"
                        + "2. Berikan pertolongan pertama (P3K) sesuai kemampuan.\n"
                        + "3. Hubungi pos SAR terdekat atau telepon 112 / Basarnas 115.\n"
                        + "4. Kirim minimal 2 orang untuk mencari bantuan jika tidak ada sinyal.\n"
                        + "5. Jangan pindahkan korban cedera tulang belakang.\n"
                        + "6. Nyalakan suar atau gunakan peluit dan cermin untuk menarik perhatian tim SAR.\n"
                        + "7. Selalu informasikan rencana pendakian ke keluarga dan pihak basecamp sebelum naik.")
                .kategori("Darurat")
                .build());

        log.info("  ✔ {} panduan keselamatan disimpan", panduanRepository.count());
        log.info("======= DataSeeder: Import data awal selesai =======");
    }

    private void seedTitik(Jalur jalur, JenisTitik jenis, String nama,
                           double lat, double lon, String deskripsi, int urutan) {
        titikJalurRepository.save(TitikJalur.builder()
                .jalur(jalur)
                .jenis(jenis)
                .nama(nama)
                .latitude(lat)
                .longitude(lon)
                .deskripsi(deskripsi)
                .urutan(urutan)
                .build());
    }
}
