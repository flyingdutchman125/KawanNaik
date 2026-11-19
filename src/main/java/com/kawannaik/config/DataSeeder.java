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
 * DataSeeder — mengimpor data awal pengguna, gunung favorit (pemula s.d. senior),
 * jalur pendakian, titik pos waypoint, rencana pendakian, dan panduan keselamatan.
 * <p>
 * Hanya dijalankan jika profil "!test" aktif.
 * Mendukung inisialisasi awal serta pembaruan otomatis jika ada master gunung yang belum ada.
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
        log.info("======= DataSeeder: Memeriksa dan menginisialisasi data awal =======");

        // 1. Pengguna (Akun Demo)
        seedPengguna();

        // 2. Master Gunung, Jalur, dan Titik Waypoint (Favorit Pemula & Senior)
        seedGunungDanJalur();

        // 3. Rencana Pendakian & Peserta
        seedRencanaDanPeserta();

        // 4. Panduan Keselamatan
        seedPanduanKeselamatan();

        log.info("======= DataSeeder: Inisialisasi data selesai =======");
    }

    // -------------------------------------------------------------------------
    // 1. Pengguna
    // -------------------------------------------------------------------------
    private void seedPengguna() {
        if (penggunaRepository.count() > 0) {
            return;
        }

        String encodedPassword = passwordEncoder.encode("password123");

        penggunaRepository.save(Pengguna.builder()
                .nama("Admin KawanNaik")
                .email("admin@kawannaik.com")
                .passwordHash(encodedPassword)
                .peran(Peran.ADMIN)
                .levelFisik(LevelFisik.MAHIR)
                .pengalaman("Berpengalaman mendaki sejak 2010 dan pengelola jalur")
                .kontakDarurat("08001234567")
                .build());

        penggunaRepository.save(Pengguna.builder()
                .nama("Budi Santoso")
                .email("budi@example.com")
                .passwordHash(encodedPassword)
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MAHIR)
                .pengalaman("Sudah mendaki 20+ gunung di Jawa, Sumatera, dan NTB")
                .kontakDarurat("08111111111")
                .build());

        penggunaRepository.save(Pengguna.builder()
                .nama("Sari Wulandari")
                .email("sari@example.com")
                .passwordHash(encodedPassword)
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.MENENGAH)
                .pengalaman("Hobi hiking sejak kuliah, 10 gunung di Jawa")
                .kontakDarurat("08122222222")
                .build());

        penggunaRepository.save(Pengguna.builder()
                .nama("Andi Pratama")
                .email("andi@example.com")
                .passwordHash(encodedPassword)
                .peran(Peran.PENDAKI)
                .levelFisik(LevelFisik.PEMULA)
                .pengalaman("Baru mulai mendaki, antusias belajar teknik mountaineering")
                .kontakDarurat("08133333333")
                .build());

        log.info("  ✔ {} pengguna disimpan (termasuk admin)", penggunaRepository.count());
    }

    // -------------------------------------------------------------------------
    // 2. Gunung, Jalur, & Titik Pos
    // -------------------------------------------------------------------------
    private void seedGunungDanJalur() {
        // ==================== A. GUNUNG FAVORIT SENIOR / MAHIR ====================

        // 1. Gunung Semeru (3.676 mdpl) - Jawa Timur
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Semeru")) {
            Gunung semeru = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Semeru")
                    .lokasi("Lumajang, Jawa Timur")
                    .ketinggianMdpl(3676)
                    .latitude(-8.1080)
                    .longitude(112.9220)
                    .kontakBasecamp("0812-3456-7890 (TNBTS Ranu Pani)")
                    .kodeWilayah("35.08.05.2002")
                    .build());

            Jalur ranuPani = jalurRepository.save(Jalur.builder()
                    .gunung(semeru)
                    .namaJalur("Ranu Pani")
                    .tingkatKesulitan("Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(12.0)
                    .build());
            seedTitik(ranuPani, JenisTitik.BASECAMP, "Basecamp Ranu Pani", -8.013, 112.952, "Titik awal pendakian, registrasi & briefing TNBTS", 1);
            seedTitik(ranuPani, JenisTitik.POS, "Pos 1 Watu Rejeng", -8.030, 112.940, "Pos peristirahatan pertama di bawah tebing batu", 2);
            seedTitik(ranuPani, JenisTitik.POS, "Ranu Kumbolo", -8.050, 112.932, "Danau air tawar indah, area mendirikan tenda utama", 3);
            seedTitik(ranuPani, JenisTitik.TITIK_RAWAN, "Tanjakan Cinta", -8.053, 112.933, "Tanjakan terjal melegenda, rawan kram otot", 4);
            seedTitik(ranuPani, JenisTitik.POS, "Pos Kalimati", -8.065, 112.930, "Camp terakhir sebelum summit attack Mahameru", 5);
            seedTitik(ranuPani, JenisTitik.SUMBER_AIR, "Sumber Mani", -8.066, 112.931, "Mata air alami bersih di hutan dekat Kalimati", 6);
            seedTitik(ranuPani, JenisTitik.TITIK_RAWAN, "Puncak Mahameru & Arcopodo", -8.108, 112.922, "Tanjakan pasir curam, waspadai gas beracun & batu gugur", 7);

            Jalur arekArek = jalurRepository.save(Jalur.builder()
                    .gunung(semeru)
                    .namaJalur("Arek-Arek")
                    .tingkatKesulitan("Sangat Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(14.0)
                    .build());
            seedTitik(arekArek, JenisTitik.BASECAMP, "Basecamp Arek-Arek", -8.010, 112.945, "Titik start jalur alternatif Semeru", 1);
            seedTitik(arekArek, JenisTitik.POS, "Pos Oro-Oro Ombo", -8.035, 112.938, "Padang savana luas bunga verbena", 2);
            seedTitik(arekArek, JenisTitik.SUMBER_AIR, "Sumber Air Ranu Kumbolo", -8.050, 112.932, "Sumber air tepi danau", 3);
            seedTitik(arekArek, JenisTitik.TITIK_RAWAN, "Tanjakan Besi", -8.070, 112.928, "Tanjakan sangat curam menuju puncak Mahameru", 4);
        }

        // 2. Gunung Rinjani (3.726 mdpl) - Lombok, NTB (Luar Jawa)
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Rinjani")) {
            Gunung rinjani = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Rinjani")
                    .lokasi("Lombok Timur, Nusa Tenggara Barat")
                    .ketinggianMdpl(3726)
                    .latitude(-8.4190)
                    .longitude(116.4580)
                    .kontakBasecamp("0819-0700-1234 (TNGR Sembalun)")
                    .kodeWilayah("52.03.09.2001")
                    .build());

            Jalur sembalun = jalurRepository.save(Jalur.builder()
                    .gunung(rinjani)
                    .namaJalur("via Sembalun")
                    .tingkatKesulitan("Sangat Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(14.0)
                    .build());
            seedTitik(sembalun, JenisTitik.BASECAMP, "Basecamp Sembalun", -8.358, 116.531, "Pintu gerbang registrasi pendakian Rinjani", 1);
            seedTitik(sembalun, JenisTitik.POS, "Pos 1 Pemantauan", -8.375, 116.515, "Pos istirahat di tengah padang savana Sembalun", 2);
            seedTitik(sembalun, JenisTitik.POS, "Pos 2 Tengengean", -8.390, 116.495, "Pos peristirahatan dengan jembatan sungai kecil", 3);
            seedTitik(sembalun, JenisTitik.POS, "Pos 3 Pada Balong", -8.402, 116.479, "Pos terakhir sebelum tanjakan ekstrem Bukit Penyesalan", 4);
            seedTitik(sembalun, JenisTitik.TITIK_RAWAN, "Bukit Penyesalan", -8.410, 116.468, "7 bukit berturut-turut dengan tanjakan sangat terjal", 5);
            seedTitik(sembalun, JenisTitik.POS, "Plawangan Sembalun", -8.416, 116.460, "Bibir kawah dengan pemandangan Danau Segara Anak & Gunung Barujari", 6);
            seedTitik(sembalun, JenisTitik.SUMBER_AIR, "Mata Air Plawangan", -8.417, 116.459, "Sumber mata air di tebing dekat Plawangan", 7);
            seedTitik(sembalun, JenisTitik.TITIK_RAWAN, "Letter E Summit Ridge", -8.418, 116.458, "Punggungan sempit berpasir licin menuju Puncak Dewi Anjani", 8);

            Jalur senaru = jalurRepository.save(Jalur.builder()
                    .gunung(rinjani)
                    .namaJalur("via Senaru")
                    .tingkatKesulitan("Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(13.0)
                    .build());
            seedTitik(senaru, JenisTitik.BASECAMP, "Pintu Masuk Senaru (Jebak Gawah)", -8.305, 116.408, "Gerbang hutan tropis lebat jalur Senaru", 1);
            seedTitik(senaru, JenisTitik.POS, "Pos 2 Montong Satas", -8.330, 116.415, "Pos tengah hutan lindung", 2);
            seedTitik(senaru, JenisTitik.POS, "Plawangan Senaru", -8.372, 116.430, "Spot spektakuler menghadap Danau Segara Anak", 3);
        }

        // 3. Gunung Kerinci (3.805 mdpl) - Jambi/Sumbar (Luar Jawa)
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Kerinci")) {
            Gunung kerinci = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Kerinci")
                    .lokasi("Kerinci, Jambi & Solok Selatan, Sumbar")
                    .ketinggianMdpl(3805)
                    .latitude(-1.6970)
                    .longitude(101.2640)
                    .kontakBasecamp("0852-6677-8899 (Pos R10 Kersik Tuo)")
                    .kodeWilayah("15.01.05.2001")
                    .build());

            Jalur kersikTuo = jalurRepository.save(Jalur.builder()
                    .gunung(kerinci)
                    .namaJalur("via Kersik Tuo")
                    .tingkatKesulitan("Sangat Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(12.0)
                    .build());
            seedTitik(kersikTuo, JenisTitik.BASECAMP, "Pintu Rimba R10", -1.745, 101.265, "Batas perkebunan teh Kayu Aro dan hutan tropis TNKS", 1);
            seedTitik(kersikTuo, JenisTitik.POS, "Pos 1 Bangku Panjang", -1.735, 101.264, "Pos peristirahatan awal", 2);
            seedTitik(kersikTuo, JenisTitik.POS, "Pos 3 Pondok Panorama", -1.720, 101.263, "Panorama kebun teh dan perbukitan Kerinci", 3);
            seedTitik(kersikTuo, JenisTitik.POS, "Shelter 1 & 2", -1.710, 101.264, "Area perkemahan di dalam hutan lebat", 4);
            seedTitik(kersikTuo, JenisTitik.TITIK_RAWAN, "Lorong Hantu", -1.705, 101.264, "Jalur lorong tanah terjal dengan akar pohon menjuntai", 5);
            seedTitik(kersikTuo, JenisTitik.POS, "Shelter 3", -1.702, 101.264, "Camp tertinggi sebelum summit attack (3.073 mdpl)", 6);
            seedTitik(kersikTuo, JenisTitik.SUMBER_AIR, "Mata Air Shelter 3", -1.703, 101.265, "Mata air rembesan jurang dekat Shelter 3", 7);
            seedTitik(kersikTuo, JenisTitik.TITIK_RAWAN, "Tugu Yudha & Puncak Indrapura", -1.697, 101.264, "Atap Sumatera 3.805 mdpl, medan cadas terjal dan bebatuan", 8);
        }

        // 4. Gunung Raung (3.344 mdpl) - Banyuwangi/Bondowoso, Jawa Timur
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Raung")) {
            Gunung raung = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Raung")
                    .lokasi("Banyuwangi-Bondowoso, Jawa Timur")
                    .ketinggianMdpl(3344)
                    .latitude(-8.1250)
                    .longitude(114.0450)
                    .kontakBasecamp("0812-4980-9988 (Basecamp Kalibaru)")
                    .kodeWilayah("35.10.02.2001")
                    .build());

            Jalur kalibaru = jalurRepository.save(Jalur.builder()
                    .gunung(raung)
                    .namaJalur("via Kalibaru")
                    .tingkatKesulitan("Ekstrem / Teknikal")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(16.0)
                    .build());
            seedTitik(kalibaru, JenisTitik.BASECAMP, "Basecamp Kalibaru", -8.210, 113.985, "Registrasi dan pengecekan perlengkapan climbing / tali temali", 1);
            seedTitik(kalibaru, JenisTitik.POS, "Pos 1 Pak Syarif", -8.190, 114.005, "Pondok singgah perkebunan kopi", 2);
            seedTitik(kalibaru, JenisTitik.POS, "Pos 4 Pondok Motor", -8.160, 114.020, "Pos peristirahatan hutan vegetasi pinus", 3);
            seedTitik(kalibaru, JenisTitik.POS, "Pos 7 Camp Terakhir", -8.138, 114.038, "Lokasi perkemahan terakhir sebelum summit attack", 4);
            seedTitik(kalibaru, JenisTitik.POS, "Puncak Bendera", -8.130, 114.040, "Titik persiapan pemakaian alat safety harness & carabiner", 5);
            seedTitik(kalibaru, JenisTitik.TITIK_RAWAN, "Jembatan Shiratal Mustaqim", -8.127, 114.043, "Punggungan batu selebar 1 meter dengan jurang 500m di kedua sisi", 6);
            seedTitik(kalibaru, JenisTitik.TITIK_RAWAN, "Puncak Sejati Raung", -8.125, 114.045, "Puncak sejati dengan pemandangan kaldera raksasa menakjubkan", 7);
        }

        // 5. Gunung Slamet (3.428 mdpl) - Jawa Tengah
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Slamet")) {
            Gunung slamet = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Slamet")
                    .lokasi("Purbalingga, Jawa Tengah")
                    .ketinggianMdpl(3428)
                    .latitude(-7.2420)
                    .longitude(109.2080)
                    .kontakBasecamp("0857-1234-5678 (Basecamp Bambangan)")
                    .kodeWilayah("33.03.10.2001")
                    .build());

            Jalur bambangan = jalurRepository.save(Jalur.builder()
                    .gunung(slamet)
                    .namaJalur("via Bambangan")
                    .tingkatKesulitan("Sangat Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(11.0)
                    .build());
            seedTitik(bambangan, JenisTitik.BASECAMP, "Basecamp Bambangan", -7.275, 109.245, "Basecamp resmi terpopuler pendakian Atap Jawa Tengah", 1);
            seedTitik(bambangan, JenisTitik.POS, "Pos 1 Pondok Gembirung", -7.265, 109.235, "Pos dengan warung dan shelter berteduh", 2);
            seedTitik(bambangan, JenisTitik.POS, "Pos 3 Pondok Cemara", -7.255, 109.225, "Hutan cemara asri dan tanah lapang camp", 3);
            seedTitik(bambangan, JenisTitik.SUMBER_AIR, "Pos 5 Samyang Rangkah", -7.248, 109.218, "Sumber mata air utama para pendaki", 4);
            seedTitik(bambangan, JenisTitik.POS, "Pos 9 Plawangan", -7.245, 109.210, "Batas vegetasi terakhir sebelum zona bebatuan", 5);
            seedTitik(bambangan, JenisTitik.TITIK_RAWAN, "Puncak Surono & Kawah Segoro Wedi", -7.242, 109.208, "Medan pasir dan batu merah labil yang curam, rawan badai es", 6);
        }


        // ==================== B. GUNUNG FAVORIT PEMULA ====================

        // 6. Gunung Prau (2.565 mdpl) - Wonosobo, Jawa Tengah
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Prau")) {
            Gunung prau = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Prau")
                    .lokasi("Wonosobo & Batang, Jawa Tengah")
                    .ketinggianMdpl(2565)
                    .latitude(-7.1856)
                    .longitude(109.9234)
                    .kontakBasecamp("0852-2891-2345 (Basecamp Patak Banteng)")
                    .kodeWilayah("33.07.12.2001")
                    .build());

            Jalur patakBanteng = jalurRepository.save(Jalur.builder()
                    .gunung(prau)
                    .namaJalur("via Patak Banteng")
                    .tingkatKesulitan("Mudah")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(3.5)
                    .build());
            seedTitik(patakBanteng, JenisTitik.BASECAMP, "Basecamp Patak Banteng", -7.208, 109.932, "Basecamp favorit pemula dengan fasilitas komplit & ojek", 1);
            seedTitik(patakBanteng, JenisTitik.POS, "Pos 1 Senthongsari", -7.200, 109.930, "Pos pertama dengan gazebo dan warung makanan", 2);
            seedTitik(patakBanteng, JenisTitik.POS, "Pos 2 Canggal Walitis", -7.195, 109.928, "Jalur tangga batu di tengah perkebunan dan pinus", 3);
            seedTitik(patakBanteng, JenisTitik.TITIK_RAWAN, "Pos 3 Cacingan", -7.190, 109.925, "Tanjakan tangga tanah berakar, licin saat musim hujan", 4);
            seedTitik(patakBanteng, JenisTitik.POS, "Sunrise Camp Teletubbies", -7.186, 109.923, "Puncak hamparan bukit rumput luas, Golden Sunrise terindah", 5);

            Jalur dieng = jalurRepository.save(Jalur.builder()
                    .gunung(prau)
                    .namaJalur("via Dieng Kulon")
                    .tingkatKesulitan("Mudah")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(4.0)
                    .build());
            seedTitik(dieng, JenisTitik.BASECAMP, "Basecamp Dieng", -7.205, 109.915, "Jalur lebih landai dan santai ramah pemula", 1);
            seedTitik(dieng, JenisTitik.POS, "Puncak Tower Dieng", -7.192, 109.918, "Tower pemancar dan padang sabana luas", 2);
        }

        // 7. Gunung Papandayan (2.665 mdpl) - Garut, Jawa Barat
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Papandayan")) {
            Gunung papandayan = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Papandayan")
                    .lokasi("Garut, Jawa Barat")
                    .ketinggianMdpl(2665)
                    .latitude(-7.3200)
                    .longitude(107.7300)
                    .kontakBasecamp("0813-2012-3456 (Pos Tiket Camp David)")
                    .kodeWilayah("32.05.08.2001")
                    .build());

            Jalur campDavid = jalurRepository.save(Jalur.builder()
                    .gunung(papandayan)
                    .namaJalur("via Camp David")
                    .tingkatKesulitan("Mudah")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(4.0)
                    .build());
            seedTitik(campDavid, JenisTitik.BASECAMP, "Basecamp Camp David", -7.315, 107.742, "Pintu gerbang wisata pendakian ramah keluarga & pemula", 1);
            seedTitik(campDavid, JenisTitik.TITIK_RAWAN, "Kawah Mas & Kawah Belerang", -7.320, 107.738, "Asap belerang aktif, wajib masker saat melintas", 2);
            seedTitik(campDavid, JenisTitik.POS, "Pondok Saladah", -7.325, 107.732, "Area camping favorit berumput dengan toilet dan warung", 3);
            seedTitik(campDavid, JenisTitik.SUMBER_AIR, "Sungai Pondok Saladah", -7.326, 107.731, "Aliran air jernih pegunungan untuk masak dan minum", 4);
            seedTitik(campDavid, JenisTitik.POS, "Hutan Mati Papandayan", -7.328, 107.728, "Pohon cantigi hangus eksotis akibat letusan masa lampau", 5);
            seedTitik(campDavid, JenisTitik.POS, "Padang Edelweis Tegal Alun", -7.332, 107.725, "Lembah tersembunyi dengan ribuan bunga edelweis abadi", 6);
        }

        // 8. Gunung Andong (1.726 mdpl) - Magelang, Jawa Tengah
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Andong")) {
            Gunung andong = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Andong")
                    .lokasi("Magelang, Jawa Tengah")
                    .ketinggianMdpl(1726)
                    .latitude(-7.3889)
                    .longitude(110.3667)
                    .kontakBasecamp("0812-3456-7891 (Basecamp Sawit)")
                    .kodeWilayah("33.08.17.2001")
                    .build());

            Jalur sawit = jalurRepository.save(Jalur.builder()
                    .gunung(andong)
                    .namaJalur("via Sawit")
                    .tingkatKesulitan("Mudah")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(2.0)
                    .build());
            seedTitik(sawit, JenisTitik.BASECAMP, "Basecamp Sawit", -7.395, 110.370, "Basecamp ramah pendaki pemula, jalur tangga tertata rapi", 1);
            seedTitik(sawit, JenisTitik.POS, "Pos 1 Kemuning", -7.392, 110.368, "Hutan pinus teduh dan sejuk", 2);
            seedTitik(sawit, JenisTitik.POS, "Pos 2 Dewa Daru", -7.390, 110.367, "Warung kopi dan istirahat sebelum punggungan puncak", 3);
            seedTitik(sawit, JenisTitik.POS, "Puncak Alap-Alap & Puncak Makam", -7.388, 110.366, "Puncak 360 derajat melihat Merbabu, Merapi, Sindoro, dan Sumbing", 4);
        }

        // 9. Gunung Ijen (2.769 mdpl) - Banyuwangi, Jawa Timur
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Ijen")) {
            Gunung ijen = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Ijen")
                    .lokasi("Banyuwangi-Bondowoso, Jawa Timur")
                    .ketinggianMdpl(2769)
                    .latitude(-8.0580)
                    .longitude(114.2420)
                    .kontakBasecamp("0819-8765-4321 (TWA Kawah Ijen Paltuding)")
                    .kodeWilayah("35.10.15.2001")
                    .build());

            Jalur paltuding = jalurRepository.save(Jalur.builder()
                    .gunung(ijen)
                    .namaJalur("via Paltuding")
                    .tingkatKesulitan("Mudah-Sedang")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(2.5)
                    .build());
            seedTitik(paltuding, JenisTitik.BASECAMP, "Pos Paltuding", -8.075, 114.225, "Pintu masuk resmi TWA Ijen, fasilitas lengkap dan persewaan masker gas", 1);
            seedTitik(paltuding, JenisTitik.POS, "Pos Bunder", -8.065, 114.235, "Pos timbang belerang para penambang tradisional", 2);
            seedTitik(paltuding, JenisTitik.POS, "Bibir Kawah Ijen", -8.058, 114.242, "Pemandangan danau asam toska terbesar di dunia", 3);
            seedTitik(paltuding, JenisTitik.TITIK_RAWAN, "Dasar Kawah Blue Fire", -8.056, 114.245, "Fenomena api biru dunia, wajib masker gas respirator", 4);
        }

        // 10. Gunung Batur (1.717 mdpl) - Kintamani, Bali (Luar Jawa)
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Batur")) {
            Gunung batur = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Batur")
                    .lokasi("Kintamani, Bangli, Bali")
                    .ketinggianMdpl(1717)
                    .latitude(-8.2420)
                    .longitude(115.3750)
                    .kontakBasecamp("0878-6543-2109 (Basecamp Toya Bungkah)")
                    .kodeWilayah("51.06.04.2001")
                    .build());

            Jalur toyaBungkah = jalurRepository.save(Jalur.builder()
                    .gunung(batur)
                    .namaJalur("via Toya Bungkah")
                    .tingkatKesulitan("Mudah")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(2.5)
                    .build());
            seedTitik(toyaBungkah, JenisTitik.BASECAMP, "Basecamp Toya Bungkah", -8.255, 115.385, "Tepi Danau Batur dengan pemandian air panas alami", 1);
            seedTitik(toyaBungkah, JenisTitik.POS, "Hutan Pinus & Batuan Vulkanik", -8.248, 115.380, "Jalur pasir vulkanik di sela pepohonan pinus", 2);
            seedTitik(toyaBungkah, JenisTitik.POS, "Puncak Sunrise Batur", -8.242, 115.375, "Puncak kawah dengan pemandangan Danau Batur & Gunung Abang", 3);
        }


        // ==================== C. GUNUNG FAVORIT MENENGAH ====================

        // 11. Gunung Merbabu (3.145 mdpl) - Jawa Tengah
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Merbabu")) {
            Gunung merbabu = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Merbabu")
                    .lokasi("Boyolali & Magelang, Jawa Tengah")
                    .ketinggianMdpl(3145)
                    .latitude(-7.4550)
                    .longitude(110.4400)
                    .kontakBasecamp("0821-3344-5566 (Basecamp Selo Baru)")
                    .kodeWilayah("33.09.11.2001")
                    .build());

            Jalur selo = jalurRepository.save(Jalur.builder()
                    .gunung(merbabu)
                    .namaJalur("via Selo")
                    .tingkatKesulitan("Sedang")
                    .levelMinimum(LevelFisik.MENENGAH)
                    .estimasiJam(7.0)
                    .build());
            seedTitik(selo, JenisTitik.BASECAMP, "Basecamp Selo", -7.495, 110.470, "Pintu masuk jalur favorit sejuta umat di lereng timur", 1);
            seedTitik(selo, JenisTitik.POS, "Pos 1 Dok-dok", -7.485, 110.465, "Pos peristirahatan awal", 2);
            seedTitik(selo, JenisTitik.POS, "Pos 3 Batu Tulis", -7.470, 110.455, "Area camp dengan pemandangan megah Gunung Merapi di seberang", 3);
            seedTitik(selo, JenisTitik.POS, "Sabana 1 & Sabana 2", -7.460, 110.445, "Hamparan bukit rumput savana hijau bergelombang yang memukau", 4);
            seedTitik(selo, JenisTitik.TITIK_RAWAN, "Puncak Triangulasi & Kenteng Songo", -7.455, 110.440, "Puncak tertinggi Merbabu dengan batu lesung bersejarah", 5);

            Jalur suwanting = jalurRepository.save(Jalur.builder()
                    .gunung(merbabu)
                    .namaJalur("via Suwanting")
                    .tingkatKesulitan("Sulit")
                    .levelMinimum(LevelFisik.MAHIR)
                    .estimasiJam(9.0)
                    .build());
            seedTitik(suwanting, JenisTitik.BASECAMP, "Basecamp Suwanting", -7.475, 110.395, "Jalur menantang dengan tanjakan terjal tiada henti", 1);
            seedTitik(suwanting, JenisTitik.POS, "Pos 3 Lembah Manding", -7.460, 110.420, "Area camp di punggungan savana terjal", 2);
        }

        // 12. Gunung Gede (2.958 mdpl) - Jawa Barat
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Gede")) {
            Gunung gede = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Gede")
                    .lokasi("Cianjur, Sukabumi & Bogor, Jawa Barat")
                    .ketinggianMdpl(2958)
                    .latitude(-6.7830)
                    .longitude(106.9830)
                    .kontakBasecamp("0811-2233-4455 (Balai TNGGP Cibodas)")
                    .kodeWilayah("32.03.02.2001")
                    .build());

            Jalur cibodas = jalurRepository.save(Jalur.builder()
                    .gunung(gede)
                    .namaJalur("via Cibodas")
                    .tingkatKesulitan("Sedang")
                    .levelMinimum(LevelFisik.MENENGAH)
                    .estimasiJam(7.5)
                    .build());
            seedTitik(cibodas, JenisTitik.BASECAMP, "Pintu Masuk Cibodas", -6.745, 107.005, "Pintu gerbang TNGGP, hutan hujan tropis basah", 1);
            seedTitik(cibodas, JenisTitik.POS, "Telaga Biru & Rawa Gayonggong", -6.755, 106.995, "Danau alami dan jembatan kayu di atas rawa", 2);
            seedTitik(cibodas, JenisTitik.TITIK_RAWAN, "Air Panas Cibeureum", -6.765, 106.990, "Tebing batu basah dengan aliran air panas belerang alami", 3);
            seedTitik(cibodas, JenisTitik.POS, "Kandang Badak", -6.775, 106.985, "Pos persimpangan Gede-Pangrango, area camp utama", 4);
            seedTitik(cibodas, JenisTitik.SUMBER_AIR, "Mata Air Kandang Badak", -6.776, 106.984, "Sumber air melimpah di dekat shelter pos", 5);
            seedTitik(cibodas, JenisTitik.POS, "Alun-Alun Suryakencana", -6.790, 106.985, "Padang edelweis legendaris seluas 50 hektar", 6);
            seedTitik(cibodas, JenisTitik.POS, "Puncak Gede & Kawah Ratu", -6.783, 106.983, "Puncak batu menghadap kawah aktif yang menakjubkan", 7);
        }

        // 13. Gunung Lawu (3.265 mdpl) - Karanganyar/Magetan
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Lawu")) {
            Gunung lawu = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Lawu")
                    .lokasi("Karanganyar (Jateng) & Magetan (Jatim)")
                    .ketinggianMdpl(3265)
                    .latitude(-7.6250)
                    .longitude(111.1920)
                    .kontakBasecamp("0822-4455-6677 (Basecamp Candi Cetho)")
                    .kodeWilayah("33.13.14.2001")
                    .build());

            Jalur cetho = jalurRepository.save(Jalur.builder()
                    .gunung(lawu)
                    .namaJalur("via Candi Cetho")
                    .tingkatKesulitan("Sedang")
                    .levelMinimum(LevelFisik.MENENGAH)
                    .estimasiJam(8.5)
                    .build());
            seedTitik(cetho, JenisTitik.BASECAMP, "Basecamp Candi Cetho", -7.595, 111.155, "Titik awal di kawasan cagar budaya Candi Hindu Cetho", 1);
            seedTitik(cetho, JenisTitik.POS, "Pos 3 Penggik", -7.608, 111.170, "Pos peristirahatan hutan cemara", 2);
            seedTitik(cetho, JenisTitik.POS, "Pos 5 Bulak Peperangan", -7.618, 111.185, "Sabana savana luas lokasi perkemahan populer", 3);
            seedTitik(cetho, JenisTitik.POS, "Gupak Menjangan & Pasar Dieng", -7.622, 111.189, "Telaga kecil musiman dan dataran batu mistis", 4);
            seedTitik(cetho, JenisTitik.POS, "Warung Mbok Yem", -7.624, 111.191, "Warung tertinggi di Indonesia pada ketinggian 3.150 mdpl", 5);
            seedTitik(cetho, JenisTitik.POS, "Puncak Hargo Dumilah", -7.625, 111.192, "Puncak tertinggi Gunung Lawu dengan tugu trigulasi", 6);
        }

        // 14. Gunung Sindoro (3.153 mdpl) - Jawa Tengah
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Sindoro")) {
            Gunung sindoro = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Sindoro")
                    .lokasi("Wonosobo & Temanggung, Jawa Tengah")
                    .ketinggianMdpl(3153)
                    .latitude(-7.3000)
                    .longitude(109.9980)
                    .kontakBasecamp("0813-9988-7766 (Basecamp Kledung)")
                    .kodeWilayah("33.23.09.2001")
                    .build());

            Jalur kledung = jalurRepository.save(Jalur.builder()
                    .gunung(sindoro)
                    .namaJalur("via Kledung")
                    .tingkatKesulitan("Sedang")
                    .levelMinimum(LevelFisik.MENENGAH)
                    .estimasiJam(6.5)
                    .build());
            seedTitik(kledung, JenisTitik.BASECAMP, "Basecamp Kledung", -7.335, 110.015, "Basecamp di perbatasan Temanggung-Wonosobo", 1);
            seedTitik(kledung, JenisTitik.POS, "Pos 2 Makam", -7.320, 110.008, "Batas kebun tembakau dan hutan pinus", 2);
            seedTitik(kledung, JenisTitik.POS, "Pos 3 Sunrise Camp", -7.310, 110.002, "Tempat camping favorit menghadap gagahnya Gunung Sumbing", 3);
            seedTitik(kledung, JenisTitik.TITIK_RAWAN, "Puncak Kawah Sindoro", -7.300, 109.998, "Kawah aktif belerang dengan padang batu eksotis", 4);
        }

        // 15. Gunung Arjuno (3.339 mdpl) - Jawa Timur
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Arjuno")) {
            Gunung arjuno = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Arjuno")
                    .lokasi("Malang-Pasuruan, Jawa Timur")
                    .ketinggianMdpl(3339)
                    .latitude(-7.7610)
                    .longitude(112.5960)
                    .kontakBasecamp("0812-1122-3344 (Basecamp Tretes)")
                    .kodeWilayah("35.07.22.2001")
                    .build());

            Jalur lawangTretes = jalurRepository.save(Jalur.builder()
                    .gunung(arjuno)
                    .namaJalur("Lawang via Tretes")
                    .tingkatKesulitan("Sedang")
                    .levelMinimum(LevelFisik.MENENGAH)
                    .estimasiJam(8.0)
                    .build());
            seedTitik(lawangTretes, JenisTitik.BASECAMP, "Basecamp Tretes", -7.720, 112.620, "Basecamp utama pendakian Arjuno dari Tretes", 1);
            seedTitik(lawangTretes, JenisTitik.POS, "Pos 1 Lalijiwo", -7.740, 112.610, "Pos peristirahatan, vegetasi hutan montane", 2);
            seedTitik(lawangTretes, JenisTitik.SUMBER_AIR, "Sumber Air Lalijiwo", -7.742, 112.608, "Sumber air bersih di area Lalijiwo", 3);
            seedTitik(lawangTretes, JenisTitik.TITIK_RAWAN, "Angin Kencang Puncak Ogal-Agil", -7.761, 112.596, "Area puncak rawan angin kencang dan sambaran petir", 4);
        }

        // 16. Gunung Welirang (3.156 mdpl) - Jawa Timur
        if (!gunungRepository.existsByNamaIgnoreCase("Gunung Welirang")) {
            Gunung welirang = gunungRepository.save(Gunung.builder()
                    .nama("Gunung Welirang")
                    .lokasi("Pasuruan, Jawa Timur")
                    .ketinggianMdpl(3156)
                    .latitude(-7.7660)
                    .longitude(112.5770)
                    .kontakBasecamp("0812-1122-3344 (Basecamp Tretes)")
                    .kodeWilayah("35.14.04.2005")
                    .build());

            Jalur viaTretes = jalurRepository.save(Jalur.builder()
                    .gunung(welirang)
                    .namaJalur("via Tretes")
                    .tingkatKesulitan("Mudah-Sedang")
                    .levelMinimum(LevelFisik.PEMULA)
                    .estimasiJam(6.0)
                    .build());
            seedTitik(viaTretes, JenisTitik.BASECAMP, "Basecamp Tretes", -7.720, 112.620, "Titik awal pendakian Welirang via Tretes", 1);
            seedTitik(viaTretes, JenisTitik.POS, "Pos 1 Air Panas", -7.735, 112.605, "Pos dekat sumber air panas alami", 2);
            seedTitik(viaTretes, JenisTitik.SUMBER_AIR, "Sumber Air Air Panas", -7.736, 112.604, "Sumber air panas belerang untuk rileks", 3);
            seedTitik(viaTretes, JenisTitik.TITIK_RAWAN, "Kawah Belerang Welirang", -7.766, 112.577, "Area kawah aktif tambang belerang, kenakan masker", 4);
        }

        log.info("  ✔ {} master gunung berhasil disimpan", gunungRepository.count());
        log.info("  ✔ {} jalur pendakian tersimpan", jalurRepository.count());
        log.info("  ✔ {} titik pos waypoint tersimpan", titikJalurRepository.count());
    }

    // -------------------------------------------------------------------------
    // 3. Rencana Pendakian & Peserta
    // -------------------------------------------------------------------------
    private void seedRencanaDanPeserta() {
        if (rencanaRepository.count() > 0) {
            return;
        }

        Pengguna budi = penggunaRepository.findByEmail("budi@example.com").orElse(null);
        Pengguna sari = penggunaRepository.findByEmail("sari@example.com").orElse(null);
        Pengguna andi = penggunaRepository.findByEmail("andi@example.com").orElse(null);

        if (budi == null || sari == null || andi == null) {
            return;
        }

        // Temukan beberapa jalur untuk rencana pendakian contoh
        jalurRepository.findAll().stream().filter(j -> j.getNamaJalur().contains("Patak Banteng")).findFirst().ifPresent(jalurPrau -> {
            RencanaPendakian rencanaPrau = rencanaRepository.save(RencanaPendakian.builder()
                    .pembuat(sari)
                    .jalur(jalurPrau)
                    .tanggalNaik(LocalDate.now().plusDays(7))
                    .kuota(6)
                    .levelMinimum(LevelFisik.PEMULA)
                    .catatan("Hiking santai menikmati sunrise Gunung Prau Dieng. Terbuka untuk pemula. Kita kumpul di Basecamp Patak Banteng Sabtu siang.")
                    .status(StatusRencana.DIBUKA)
                    .build());
            pesertaRepository.save(PesertaRencana.builder().rencana(rencanaPrau).pengguna(andi).status(StatusPermintaan.DITERIMA).build());
        });

        jalurRepository.findAll().stream().filter(j -> j.getNamaJalur().contains("Sembalun")).findFirst().ifPresent(jalurRinjani -> {
            RencanaPendakian rencanaRinjani = rencanaRepository.save(RencanaPendakian.builder()
                    .pembuat(budi)
                    .jalur(jalurRinjani)
                    .tanggalNaik(LocalDate.now().plusDays(21))
                    .kuota(4)
                    .levelMinimum(LevelFisik.MAHIR)
                    .catatan("Ekspedisi Rinjani 4D3N via Sembalun tembus Senaru. Wajib fisik prima, bawa perlengkapan standar summit dingin dan tenda windproof.")
                    .status(StatusRencana.DIBUKA)
                    .build());
            pesertaRepository.save(PesertaRencana.builder().rencana(rencanaRinjani).pengguna(sari).status(StatusPermintaan.MENUNGGU).build());
        });

        jalurRepository.findAll().stream().filter(j -> j.getNamaJalur().contains("Camp David")).findFirst().ifPresent(jalurPapandayan -> {
            RencanaPendakian rencanaPapandayan = rencanaRepository.save(RencanaPendakian.builder()
                    .pembuat(sari)
                    .jalur(jalurPapandayan)
                    .tanggalNaik(LocalDate.now().plusDays(14))
                    .kuota(10)
                    .levelMinimum(LevelFisik.PEMULA)
                    .catatan("Camp ceria Papandayan di Pondok Saladah dan foto di Hutan Mati. Cocok untuk first-time hiker.")
                    .status(StatusRencana.DIBUKA)
                    .build());
            pesertaRepository.save(PesertaRencana.builder().rencana(rencanaPapandayan).pengguna(budi).status(StatusPermintaan.DITERIMA).build());
            pesertaRepository.save(PesertaRencana.builder().rencana(rencanaPapandayan).pengguna(andi).status(StatusPermintaan.DITERIMA).build());
        });

        log.info("  ✔ {} rencana pendakian contoh disimpan", rencanaRepository.count());
    }

    // -------------------------------------------------------------------------
    // 4. Panduan Keselamatan
    // -------------------------------------------------------------------------
    private void seedPanduanKeselamatan() {
        if (panduanRepository.count() > 0) {
            return;
        }

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Pencegahan dan Penanganan Hipotermia")
                .isi("Hipotermia terjadi saat suhu inti tubuh turun di bawah 35°C. Langkah pencegahan:\n"
                        + "1. Sistem 3-Layer: Base layer (dry-fit sintetis/wol merino), mid-layer (fleece penghangat), outer layer (jaket windproof & waterproof).\n"
                        + "2. Jangan pernah memakai pakaian jeans/katun karena menyerap air dan lama kering.\n"
                        + "3. Selalu simpan pakaian ganti dan sleeping bag di dalam kantong plastik/dry bag di dalam ransel.\n"
                        + "4. Asup kalori tinggi dan minuman manis hangat secara teratur selama pendakian.\n"
                        + "5. Jika rekan mulai menggigil hebat atau bicaranya meracau: segera masukkan ke dalam sleeping bag di tenda kering, ganti pakaian basahnya, dan beri penghangat tubuh.")
                .kategori("Kesehatan")
                .build());

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Mengenali dan Mengatasi Acute Mountain Sickness (AMS)")
                .isi("Acute Mountain Sickness (AMS) rentan menyerang pendaki di ketinggian > 2.500 mdpl (seperti Semeru, Slamet, Rinjani, Kerinci).\n"
                        + "Gejala:\n"
                        + "- Sakit kepala berdenyut, mual, pusing, lemas berlebih, dan sesak nafas ringan.\n"
                        + "Pencegahan:\n"
                        + "1. Lakukan aklimatisasi dengan bermalam di basecamp atau pos awal sebelum mendaki cepat.\n"
                        + "2. 'Climb high, sleep low': naik bertahap, jangan tergesa-gesa berlari mengejar waktu.\n"
                        + "3. Minum air putih minimal 3-4 liter per hari untuk menjaga sirkulasi oksigen darah.\n"
                        + "Penanganan:\n"
                        + "- HUKUM UTAMA: Jika gejala memberat atau muncul ataksia (jalan sempoyongan), SEGERA EVAKUASI TURUN KE KETINGGIAN LEBIH RENDAH! Jangan paksakan summit.")
                .kategori("Kesehatan")
                .build());

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Tips Navigasi Darat dan Orientasi Medan")
                .isi("Tersesat di jalur adalah salah satu penyebab insiden terbanyak di gunung Indonesia.\n"
                        + "1. Pelajari kontur dan topografi gunung sebelum mendaki (unduh peta digital offline / GPX di KawanNaik).\n"
                        + "2. Bawa kompas bidik fisik dan peluit di tali ransel yang mudah dijangkau.\n"
                        + "3. Perhatikan percabangan jalur dan penanda jalan (pita warna, plang pos, cat di batu).\n"
                        + "4. Jangan pernah memotong jalur resmi (shortcut switchback) karena selain merusak vegetasi, berisiko jatuh ke jurang curam.\n"
                        + "5. Jika tersesat, terapkan protokol S-T-O-P (Sit, Think, Observe, Plan). Tetap di jalur terakhir yang dikenali.")
                .kategori("Navigasi")
                .build());

        panduanRepository.save(PanduanKeselamatan.builder()
                .judul("Etika dan Prinsip Leave No Trace (LNT)")
                .isi("Sebagai pendaki bertanggung jawab, hormati alam dan warga lokal:\n"
                        + "1. Sampahmu tanggung jawabmu — bawa turun SEMUA sampah termasuk puntung rokok dan tisu basah.\n"
                        + "2. Jangan memetik bunga edelweis atau merusak flora dan fauna langka.\n"
                        + "3. Buat api unggun hanya di tempat yang diizinkan dan pastikan padam total sebelum ditinggal.\n"
                        + "4. Hormati adat istiadat setempat dan kesakralan situs/makam di gunung.\n"
                        + "5. Buang air besar/kecil minimal 60 meter dari sumber mata air dan jalur pendakian (gali lubang sedalam 15-20 cm dan kubur kembali).")
                .kategori("Etika")
                .build());

        log.info("  ✔ {} panduan keselamatan disimpan", panduanRepository.count());
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
