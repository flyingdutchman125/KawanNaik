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
 * DataSeeder — mengimpor data awal pengguna, 16 gunung favorit di Indonesia,
 * jalur-jalur pendakian resmi di setiap gunung, titik pos waypoint, serta rencana pendakian.
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
        log.info("======= DataSeeder: Memeriksa dan menginisialisasi master data KawanNaik =======");

        seedPengguna();
        seedGunungDanJalurLengkap();
        seedRencanaDanPeserta();
        seedPanduanKeselamatan();

        log.info("======= DataSeeder: Inisialisasi data selesai =======");
    }

    // -------------------------------------------------------------------------
    // 1. Pengguna (Akun Demo)
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

        log.info("  ✔ {} pengguna tersimpan", penggunaRepository.count());
    }

    // -------------------------------------------------------------------------
    // 2. Master Gunung & Jalur Pendakian Lengkap
    // -------------------------------------------------------------------------
    private void seedGunungDanJalurLengkap() {
        // 1. Gunung Semeru (3.676 mdpl) - Jawa Timur
        Gunung semeru = getOrCreateGunung("Gunung Semeru", "Lumajang, Jawa Timur", 3676, -8.1080, 112.9220, "0812-3456-7890 (TNBTS Ranu Pani)", "35.08.05.2002");
        initJalur(semeru, "Ranu Pani", "Sulit", LevelFisik.MAHIR, 12.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Ranu Pani", -8.013, 112.952, "Titik awal pendakian, registrasi & briefing TNBTS", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Watu Rejeng", -8.030, 112.940, "Pos peristirahatan pertama di bawah tebing batu", 2);
            seedTitik(j, JenisTitik.POS, "Ranu Kumbolo", -8.050, 112.932, "Danau air tawar indah, area mendirikan tenda utama", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Tanjakan Cinta", -8.053, 112.933, "Tanjakan terjal melegenda, rawan kram otot", 4);
            seedTitik(j, JenisTitik.POS, "Pos Kalimati", -8.065, 112.930, "Camp terakhir sebelum summit attack Mahameru", 5);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Sumber Mani", -8.066, 112.931, "Mata air alami bersih di hutan dekat Kalimati", 6);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Puncak Mahameru & Arcopodo", -8.108, 112.922, "Tanjakan pasir curam, waspadai gas beracun & batu gugur", 7);
        });
        initJalur(semeru, "Arek-Arek", "Sangat Sulit", LevelFisik.MAHIR, 14.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Arek-Arek", -8.010, 112.945, "Titik start jalur alternatif Semeru", 1);
            seedTitik(j, JenisTitik.POS, "Pos Oro-Oro Ombo", -8.035, 112.938, "Padang savana luas bunga verbena", 2);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Sumber Air Ranu Kumbolo", -8.050, 112.932, "Sumber air tepi danau", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Tanjakan Besi", -8.070, 112.928, "Tanjakan sangat curam menuju puncak Mahameru", 4);
        });
        initJalur(semeru, "via Tawon Songo", "Ekstrem / Petualangan", LevelFisik.MAHIR, 15.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pos Pasrujambe Tawon Songo", -8.145, 112.985, "Pos jalur selatan Lumajang melewati hutan lindung rapat", 1);
            seedTitik(j, JenisTitik.POS, "Pos Kali Kebo", -8.132, 112.960, "Pos lembah sungai berbatu", 2);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Punggungan Cadas Selatan", -8.118, 112.940, "Punggungan jurang terjal rawan longsor lahar dingin", 3);
        });

        // 2. Gunung Rinjani (3.726 mdpl) - Lombok, NTB (Luar Jawa)
        Gunung rinjani = getOrCreateGunung("Gunung Rinjani", "Lombok Timur, Nusa Tenggara Barat", 3726, -8.4190, 116.4580, "0819-0700-1234 (TNGR Sembalun)", "52.03.09.2001");
        initJalur(rinjani, "via Sembalun", "Sangat Sulit", LevelFisik.MAHIR, 14.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Sembalun", -8.358, 116.531, "Pintu gerbang registrasi pendakian Rinjani", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Pemantauan", -8.375, 116.515, "Pos istirahat di tengah padang savana Sembalun", 2);
            seedTitik(j, JenisTitik.POS, "Pos 2 Tengengean", -8.390, 116.495, "Pos peristirahatan dengan jembatan sungai kecil", 3);
            seedTitik(j, JenisTitik.POS, "Pos 3 Pada Balong", -8.402, 116.479, "Pos terakhir sebelum tanjakan ekstrem Bukit Penyesalan", 4);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Bukit Penyesalan", -8.410, 116.468, "7 bukit berturut-turut dengan tanjakan sangat terjal", 5);
            seedTitik(j, JenisTitik.POS, "Plawangan Sembalun", -8.416, 116.460, "Bibir kawah dengan pemandangan Danau Segara Anak & Gunung Barujari", 6);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Mata Air Plawangan", -8.417, 116.459, "Sumber mata air di tebing dekat Plawangan", 7);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Letter E Summit Ridge", -8.418, 116.458, "Punggungan sempit berpasir licin menuju Puncak Dewi Anjani", 8);
        });
        initJalur(rinjani, "via Senaru", "Sulit", LevelFisik.MAHIR, 13.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pintu Masuk Senaru (Jebak Gawah)", -8.305, 116.408, "Gerbang hutan tropis lebat jalur Senaru", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Montong Satas", -8.330, 116.415, "Pos tengah hutan lindung", 2);
            seedTitik(j, JenisTitik.POS, "Plawangan Senaru", -8.372, 116.430, "Spot spektakuler menghadap Danau Segara Anak", 3);
        });
        initJalur(rinjani, "via Torean", "Sulit / Petualangan", LevelFisik.MAHIR, 12.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Desa Wisata Torean", -8.290, 116.440, "Pintu masuk lembah purba jalur Torean", 1);
            seedTitik(j, JenisTitik.POS, "Birisan Nangka", -8.320, 116.445, "Pos hutan basah dan perkebunan kopi", 2);
            seedTitik(j, JenisTitik.POS, "Lembah Kokok Puteh & Gua Susu", -8.360, 116.448, "Air terjun megah mengalir ke sungai belerang putih", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Tebing Propok Torean", -8.375, 116.442, "Tebing jurang sempit berpagar besi di atas sungai", 4);
        });
        initJalur(rinjani, "via Aik Berik", "Sedang-Sulit", LevelFisik.MENENGAH, 11.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pintu Masuk Aik Berik (Lombok Tengah)", -8.502, 116.325, "Jalur alternatif selatan dengan air terjun Benang Stokel", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Telaga", -8.460, 116.365, "Area camp telaga alami di dalam hutan tropis", 2);
            seedTitik(j, JenisTitik.POS, "Plawangan Aik Berik", -8.435, 116.420, "Tepi kaldera selatan Rinjani", 3);
        });

        // 3. Gunung Kerinci (3.805 mdpl) - Jambi / Sumbar (Luar Jawa)
        Gunung kerinci = getOrCreateGunung("Gunung Kerinci", "Kerinci, Jambi & Solok Selatan, Sumbar", 3805, -1.6970, 101.2640, "0852-6677-8899 (Pos R10 Kersik Tuo)", "15.01.05.2001");
        initJalur(kerinci, "via Kersik Tuo", "Sangat Sulit", LevelFisik.MAHIR, 12.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pintu Rimba R10", -1.745, 101.265, "Batas perkebunan teh Kayu Aro dan hutan tropis TNKS", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Bangku Panjang", -1.735, 101.264, "Pos peristirahatan awal", 2);
            seedTitik(j, JenisTitik.POS, "Pos 3 Pondok Panorama", -1.720, 101.263, "Panorama kebun teh dan perbukitan Kerinci", 3);
            seedTitik(j, JenisTitik.POS, "Shelter 1 & 2", -1.710, 101.264, "Area perkemahan di dalam hutan lebat", 4);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Lorong Hantu", -1.705, 101.264, "Jalur lorong tanah terjal dengan akar pohon menjuntai", 5);
            seedTitik(j, JenisTitik.POS, "Shelter 3", -1.702, 101.264, "Camp tertinggi sebelum summit attack (3.073 mdpl)", 6);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Mata Air Shelter 3", -1.703, 101.265, "Mata air rembesan jurang dekat Shelter 3", 7);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Tugu Yudha & Puncak Indrapura", -1.697, 101.264, "Atap Sumatera 3.805 mdpl, medan cadas terjal dan bebatuan", 8);
        });
        initJalur(kerinci, "via Solok Selatan (Bangun Rejo)", "Ekstrem / Ekspedisi", LevelFisik.MAHIR, 18.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Bangun Rejo Solok Selatan", -1.650, 101.280, "Titik awal ekspedisi rimba sisi Sumatera Barat", 1);
            seedTitik(j, JenisTitik.POS, "Pos Camp Rimba Lumut", -1.670, 101.272, "Hutan lumut purba yang sangat lebat dan sejuk", 2);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Punggungan Batu Cadas Utara", -1.685, 101.268, "Jalur cadas berbatu terjal menyambung ke puncak", 3);
        });

        // 4. Gunung Raung (3.344 mdpl) - Banyuwangi / Bondowoso, Jawa Timur
        Gunung raung = getOrCreateGunung("Gunung Raung", "Banyuwangi-Bondowoso, Jawa Timur", 3344, -8.1250, 114.0450, "0812-4980-9988 (Basecamp Kalibaru)", "35.10.02.2001");
        initJalur(raung, "via Kalibaru", "Ekstrem / Teknikal", LevelFisik.MAHIR, 16.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Kalibaru", -8.210, 113.985, "Registrasi dan pengecekan perlengkapan climbing / tali temali", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Pak Syarif", -8.190, 114.005, "Pondok singgah perkebunan kopi", 2);
            seedTitik(j, JenisTitik.POS, "Pos 4 Pondok Motor", -8.160, 114.020, "Pos peristirahatan hutan vegetasi pinus", 3);
            seedTitik(j, JenisTitik.POS, "Pos 7 Camp Terakhir", -8.138, 114.038, "Lokasi perkemahan terakhir sebelum summit attack", 4);
            seedTitik(j, JenisTitik.POS, "Puncak Bendera", -8.130, 114.040, "Titik persiapan pemakaian alat safety harness & carabiner", 5);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Jembatan Shiratal Mustaqim", -8.127, 114.043, "Punggungan batu selebar 1 meter dengan jurang 500m di kedua sisi", 6);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Puncak Sejati Raung", -8.125, 114.045, "Puncak sejati dengan pemandangan kaldera raksasa menakjubkan", 7);
        });
        initJalur(raung, "via Sumberwringin", "Sulit", LevelFisik.MAHIR, 12.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Sumberwringin Bondowoso", -7.985, 114.120, "Jalur utara legendaris sebelum jalur Kalibaru dibuka", 1);
            seedTitik(j, JenisTitik.POS, "Pondok Motor Sumberwringin", -8.065, 114.085, "Batas kebun pinus dan hutan pegunungan", 2);
            seedTitik(j, JenisTitik.POS, "Puncak Bayangan Raung", -8.115, 114.055, "Bibir kaldera utara menghadap kawah raksasa", 3);
        });

        // 5. Gunung Slamet (3.428 mdpl) - Jawa Tengah
        Gunung slamet = getOrCreateGunung("Gunung Slamet", "Purbalingga, Jawa Tengah", 3428, -7.2420, 109.2080, "0857-1234-5678 (Basecamp Bambangan)", "33.03.10.2001");
        initJalur(slamet, "via Bambangan", "Sangat Sulit", LevelFisik.MAHIR, 11.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Bambangan", -7.275, 109.245, "Basecamp resmi terpopuler pendakian Atap Jawa Tengah", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Pondok Gembirung", -7.265, 109.235, "Pos dengan warung dan shelter berteduh", 2);
            seedTitik(j, JenisTitik.POS, "Pos 3 Pondok Cemara", -7.255, 109.225, "Hutan cemara asri dan tanah lapang camp", 3);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Pos 5 Samyang Rangkah", -7.248, 109.218, "Sumber mata air utama para pendaki", 4);
            seedTitik(j, JenisTitik.POS, "Pos 9 Plawangan", -7.245, 109.210, "Batas vegetasi terakhir sebelum zona bebatuan", 5);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Puncak Surono & Kawah Segoro Wedi", -7.242, 109.208, "Medan pasir dan batu merah labil yang curam, rawan badai es", 6);
        });
        initJalur(slamet, "via Guci (Tegal)", "Sulit", LevelFisik.MAHIR, 12.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Permadi Guci", -7.185, 109.168, "Titik awal dekat pemandian air panas alami Guci", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Amreta", -7.215, 109.182, "Pos hutan pinus dengan sumber air jernih", 2);
            seedTitik(j, JenisTitik.POS, "Plawangan Guci", -7.235, 109.198, "Batas punggungan utara menuju puncak Slamet", 3);
        });
        initJalur(slamet, "via Dipajaya (Pemalang)", "Sulit", LevelFisik.MAHIR, 11.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Dipajaya Clekatakan", -7.212, 109.240, "Jalur timur laut Pemalang yang teduh dan asri", 1);
            seedTitik(j, JenisTitik.POS, "Pos 4 Igir-igir", -7.228, 109.225, "Punggungan savana menyatu dengan jalur Bambangan", 2);
        });
        initJalur(slamet, "via Baturraden (Banyumas)", "Sangat Sulit", LevelFisik.MAHIR, 13.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Baturraden (Wana Wisata)", -7.310, 109.220, "Jalur selatan Banyumas dengan hutan lumut basah lebat", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Pelangkah", -7.280, 109.215, "Pos peristirahatan hutan hujan pegunungan", 2);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Plawangan Selatan", -7.250, 109.210, "Batu terjal curam sebelum mencapai bibir kawah", 3);
        });

        // 6. Gunung Prau (2.565 mdpl) - Wonosobo, Jawa Tengah
        Gunung prau = getOrCreateGunung("Gunung Prau", "Wonosobo & Batang, Jawa Tengah", 2565, -7.1856, 109.9234, "0852-2891-2345 (Basecamp Patak Banteng)", "33.07.12.2001");
        initJalur(prau, "via Patak Banteng", "Mudah", LevelFisik.PEMULA, 3.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Patak Banteng", -7.208, 109.932, "Basecamp favorit pemula dengan fasilitas komplit & ojek", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Senthongsari", -7.200, 109.930, "Pos pertama dengan gazebo dan warung makanan", 2);
            seedTitik(j, JenisTitik.POS, "Pos 2 Canggal Walitis", -7.195, 109.928, "Jalur tangga batu di tengah perkebunan dan pinus", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Pos 3 Cacingan", -7.190, 109.925, "Tanjakan tangga tanah berakar, licin saat musim hujan", 4);
            seedTitik(j, JenisTitik.POS, "Sunrise Camp Teletubbies", -7.186, 109.923, "Puncak hamparan bukit rumput luas, Golden Sunrise terindah", 5);
        });
        initJalur(prau, "via Dieng Kulon", "Mudah", LevelFisik.PEMULA, 4.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Dieng Kulon (Dwarawati)", -7.205, 109.915, "Jalur lebih landai dan santai ramah pemula dekat Candi", 1);
            seedTitik(j, JenisTitik.POS, "Puncak Tower Dieng", -7.192, 109.918, "Tower pemancar dan padang sabana luas", 2);
        });
        initJalur(prau, "via Kalilembu", "Mudah", LevelFisik.PEMULA, 3.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Kalilembu Dieng", -7.210, 109.925, "Alternatif favorit pemula, jalur landai kebun kentang", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Telaga Wurung", -7.198, 109.922, "Area istirahat teduh pepohonan pinus", 2);
        });
        initJalur(prau, "via Wates (Temanggung)", "Mudah-Sedang", LevelFisik.PEMULA, 4.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Wates Wonoboyo", -7.165, 109.965, "Jalur lereng timur menyusuri perkebunan teh yang asri", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Plawangan Wates", -7.178, 109.940, "Pemandangan bukit hijau dan danau telaga", 2);
        });

        // 7. Gunung Papandayan (2.665 mdpl) - Garut, Jawa Barat
        Gunung papandayan = getOrCreateGunung("Gunung Papandayan", "Garut, Jawa Barat", 2665, -7.3200, 107.7300, "0813-2012-3456 (Pos Tiket Camp David)", "32.05.08.2001");
        initJalur(papandayan, "via Camp David", "Mudah", LevelFisik.PEMULA, 4.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Camp David", -7.315, 107.742, "Pintu gerbang wisata pendakian ramah keluarga & pemula", 1);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Kawah Mas & Kawah Belerang", -7.320, 107.738, "Asap belerang aktif, wajib masker saat melintas", 2);
            seedTitik(j, JenisTitik.POS, "Pondok Saladah", -7.325, 107.732, "Area camping favorit berumput dengan toilet dan warung", 3);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Sungai Pondok Saladah", -7.326, 107.731, "Aliran air jernih pegunungan untuk masak dan minum", 4);
            seedTitik(j, JenisTitik.POS, "Hutan Mati Papandayan", -7.328, 107.728, "Pohon cantigi hangus eksotis akibat letusan masa lampau", 5);
            seedTitik(j, JenisTitik.POS, "Padang Edelweis Tegal Alun", -7.332, 107.725, "Lembah tersembunyi dengan ribuan bunga edelweis abadi", 6);
        });
        initJalur(papandayan, "via Cisurupan", "Mudah-Sedang", LevelFisik.PEMULA, 4.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pos Cisurupan", -7.305, 107.750, "Pintu masuk dari kaki bukit Cisurupan", 1);
            seedTitik(j, JenisTitik.POS, "Kawah Baru Papandayan", -7.318, 107.735, "Kawah baru dengan bebatuan warna-warni", 2);
        });

        // 8. Gunung Andong (1.726 mdpl) - Magelang, Jawa Tengah
        Gunung andong = getOrCreateGunung("Gunung Andong", "Magelang, Jawa Tengah", 1726, -7.3889, 110.3667, "0812-3456-7891 (Basecamp Sawit)", "33.08.17.2001");
        initJalur(andong, "via Sawit", "Mudah", LevelFisik.PEMULA, 2.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Sawit", -7.395, 110.370, "Basecamp ramah pendaki pemula, jalur tangga tertata rapi", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Kemuning", -7.392, 110.368, "Hutan pinus teduh dan sejuk", 2);
            seedTitik(j, JenisTitik.POS, "Pos 2 Dewa Daru", -7.390, 110.367, "Warung kopi dan istirahat sebelum punggungan puncak", 3);
            seedTitik(j, JenisTitik.POS, "Puncak Alap-Alap & Puncak Makam", -7.388, 110.366, "Puncak 360 derajat melihat Merbabu, Merapi, Sindoro, dan Sumbing", 4);
        });
        initJalur(andong, "via Pendem", "Mudah", LevelFisik.PEMULA, 2.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Pendem Girirejo", -7.398, 110.365, "Jalur tangga semen rapi dan area parkir luas", 1);
            seedTitik(j, JenisTitik.POS, "Punggung Naga Andong", -7.390, 110.365, "Punggungan rumput asri menyambung ke puncak makam", 2);
        });
        initJalur(andong, "via Gogik", "Mudah", LevelFisik.PEMULA, 2.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Gogik", -7.385, 110.375, "Jalur lereng timur yang asri dan sejuk", 1);
            seedTitik(j, JenisTitik.POS, "Puncak Jiwa", -7.388, 110.368, "Salah satu puncak panorama Andong", 2);
        });

        // 9. Gunung Ijen (2.769 mdpl) - Banyuwangi, Jawa Timur
        Gunung ijen = getOrCreateGunung("Gunung Ijen", "Banyuwangi-Bondowoso, Jawa Timur", 2769, -8.0580, 114.2420, "0819-8765-4321 (TWA Kawah Ijen Paltuding)", "35.10.15.2001");
        initJalur(ijen, "via Paltuding", "Mudah-Sedang", LevelFisik.PEMULA, 2.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pos Paltuding", -8.075, 114.225, "Pintu masuk resmi TWA Ijen, fasilitas lengkap dan persewaan masker gas", 1);
            seedTitik(j, JenisTitik.POS, "Pos Bunder", -8.065, 114.235, "Pos timbang belerang para penambang tradisional", 2);
            seedTitik(j, JenisTitik.POS, "Bibir Kawah Ijen", -8.058, 114.242, "Pemandangan danau asam toska terbesar di dunia", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Dasar Kawah Blue Fire", -8.056, 114.245, "Fenomena api biru dunia, wajib masker gas respirator", 4);
        });
        initJalur(ijen, "via Taman Dadar / Rembangan", "Sedang", LevelFisik.MENENGAH, 5.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Perkebunan Rembangan", -8.030, 114.180, "Trekking menyusuri perkebunan kopi dan hutan cemara", 1);
            seedTitik(j, JenisTitik.POS, "Puncak Merapi Ijen", -8.050, 114.220, "Puncak gunung mati di samping Kawah Ijen", 2);
        });

        // 10. Gunung Batur (1.717 mdpl) - Kintamani, Bali (Luar Jawa)
        Gunung batur = getOrCreateGunung("Gunung Batur", "Kintamani, Bangli, Bali", 1717, -8.2420, 115.3750, "0878-6543-2109 (Basecamp Toya Bungkah)", "51.06.04.2001");
        initJalur(batur, "via Toya Bungkah", "Mudah", LevelFisik.PEMULA, 2.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Toya Bungkah", -8.255, 115.385, "Tepi Danau Batur dengan pemandian air panas alami", 1);
            seedTitik(j, JenisTitik.POS, "Hutan Pinus & Batuan Vulkanik", -8.248, 115.380, "Jalur pasir vulkanik di sela pepohonan pinus", 2);
            seedTitik(j, JenisTitik.POS, "Puncak Sunrise Batur", -8.242, 115.375, "Puncak kawah dengan pemandangan Danau Batur & Gunung Abang", 3);
        });
        initJalur(batur, "via Pasar Agung", "Mudah", LevelFisik.PEMULA, 2.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pura Pasar Agung Batur", -8.250, 115.365, "Jalur tercepat dan populer untuk pemburu sunrise", 1);
            seedTitik(j, JenisTitik.POS, "Puncak Kawah Kedua Batur", -8.244, 115.372, "Bibir kaldera dengan uap panas vulkanik", 2);
        });

        // 11. Gunung Merbabu (3.145 mdpl) - Jawa Tengah
        Gunung merbabu = getOrCreateGunung("Gunung Merbabu", "Boyolali & Magelang, Jawa Tengah", 3145, -7.4550, 110.4400, "0821-3344-5566 (Basecamp Selo Baru)", "33.09.11.2001");
        initJalur(merbabu, "via Selo", "Sedang", LevelFisik.MENENGAH, 7.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Selo", -7.495, 110.470, "Pintu masuk jalur favorit sejuta umat di lereng timur", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Dok-dok", -7.485, 110.465, "Pos peristirahatan awal", 2);
            seedTitik(j, JenisTitik.POS, "Pos 3 Batu Tulis", -7.470, 110.455, "Area camp dengan pemandangan megah Gunung Merapi di seberang", 3);
            seedTitik(j, JenisTitik.POS, "Sabana 1 & Sabana 2", -7.460, 110.445, "Hamparan bukit rumput savana hijau bergelombang yang memukau", 4);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Puncak Triangulasi & Kenteng Songo", -7.455, 110.440, "Puncak tertinggi Merbabu dengan batu lesung bersejarah", 5);
        });
        initJalur(merbabu, "via Suwanting", "Sulit", LevelFisik.MAHIR, 9.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Suwanting", -7.475, 110.395, "Jalur menantang dengan tanjakan terjal tiada henti", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Lembah Manding", -7.460, 110.420, "Area camp di punggungan savana terjal", 2);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Puncak Suwanting & Triangulasi", -7.455, 110.440, "Puncak bukit savana terbuka terpaan angin kencang", 3);
        });
        initJalur(merbabu, "via Wekas", "Sedang", LevelFisik.MENENGAH, 7.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Wekas Magelang", -7.445, 110.405, "Jalur air melimpah favorit pendaki santai", 1);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Pos 2 Wekas (Pipa Air Alami)", -7.450, 110.420, "Area camp luas dengan kran air bersih pegunungan", 2);
            seedTitik(j, JenisTitik.POS, "Kawah Mati & Pertigaan Helipad", -7.452, 110.435, "Pertemuan jalur Wekas, Thekelan, dan Cuntel", 3);
        });
        initJalur(merbabu, "via Thekelan", "Sedang-Sulit", LevelFisik.MENENGAH, 8.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Thekelan Kopeng", -7.410, 110.430, "Jalur klasik utara Kopeng", 1);
            seedTitik(j, JenisTitik.POS, "Pos 4 Watu Gubug", -7.435, 110.435, "Batu berlubang mirip gua kecil tempat berlindung", 2);
            seedTitik(j, JenisTitik.POS, "Puncak Pemancar", -7.448, 110.438, "Stasiun pemancar radio tua di ketinggian 3.000 mdpl", 3);
        });

        // 12. Gunung Gede (2.958 mdpl) - Jawa Barat
        Gunung gede = getOrCreateGunung("Gunung Gede", "Cianjur, Sukabumi & Bogor, Jawa Barat", 2958, -6.7830, 106.9830, "0811-2233-4455 (Balai TNGGP Cibodas)", "32.03.02.2001");
        initJalur(gede, "via Cibodas", "Sedang", LevelFisik.MENENGAH, 7.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pintu Masuk Cibodas", -6.745, 107.005, "Pintu gerbang TNGGP, hutan hujan tropis basah", 1);
            seedTitik(j, JenisTitik.POS, "Telaga Biru & Rawa Gayonggong", -6.755, 106.995, "Danau alami dan jembatan kayu di atas rawa", 2);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Air Panas Cibeureum", -6.765, 106.990, "Tebing batu basah dengan aliran air panas belerang alami", 3);
            seedTitik(j, JenisTitik.POS, "Kandang Badak", -6.775, 106.985, "Pos persimpangan Gede-Pangrango, area camp utama", 4);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Mata Air Kandang Badak", -6.776, 106.984, "Sumber air melimpah di dekat shelter pos", 5);
            seedTitik(j, JenisTitik.POS, "Alun-Alun Suryakencana", -6.790, 106.985, "Padang edelweis legendaris seluas 50 hektar", 6);
            seedTitik(j, JenisTitik.POS, "Puncak Gede & Kawah Ratu", -6.783, 106.983, "Puncak batu menghadap kawah aktif yang menakjubkan", 7);
        });
        initJalur(gede, "via Gunung Putri", "Sedang-Sulit", LevelFisik.MENENGAH, 6.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Gunung Putri", -6.760, 107.025, "Jalur tercepat menuju padang edelweis Suryakencana", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Mondokan", -6.775, 107.005, "Tanjakan akar tanah terjal hutan pinus", 2);
            seedTitik(j, JenisTitik.POS, "Gerbang Suryakencana Timur", -6.788, 106.990, "Pintu masuk timur Alun-Alun Suryakencana", 3);
        });
        initJalur(gede, "via Selabintana", "Sulit", LevelFisik.MAHIR, 10.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Selabintana Sukabumi", -6.850, 106.960, "Jalur petualangan rimba selatan Sukabumi", 1);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Sungai Cibeureum & Hutan Pacet", -6.820, 106.970, "Hutan basah lebat habitat lintah dan pacet", 2);
            seedTitik(j, JenisTitik.POS, "Alun-Alun Suryakencana Barat", -6.795, 106.980, "Ujung barat padang savana Suryakencana", 3);
        });

        // 13. Gunung Lawu (3.265 mdpl) - Karanganyar/Magetan
        Gunung lawu = getOrCreateGunung("Gunung Lawu", "Karanganyar (Jateng) & Magetan (Jatim)", 3265, -7.6250, 111.1920, "0822-4455-6677 (Basecamp Candi Cetho)", "33.13.14.2001");
        initJalur(lawu, "via Candi Cetho", "Sedang", LevelFisik.MENENGAH, 8.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Candi Cetho", -7.595, 111.155, "Titik awal di kawasan cagar budaya Candi Hindu Cetho", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Penggik", -7.608, 111.170, "Pos peristirahatan hutan cemara", 2);
            seedTitik(j, JenisTitik.POS, "Pos 5 Bulak Peperangan", -7.618, 111.185, "Sabana savana luas lokasi perkemahan populer", 3);
            seedTitik(j, JenisTitik.POS, "Gupak Menjangan & Pasar Dieng", -7.622, 111.189, "Telaga kecil musiman dan dataran batu mistis", 4);
            seedTitik(j, JenisTitik.POS, "Warung Mbok Yem", -7.624, 111.191, "Warung tertinggi di Indonesia pada ketinggian 3.150 mdpl", 5);
            seedTitik(j, JenisTitik.POS, "Puncak Hargo Dumilah", -7.625, 111.192, "Puncak tertinggi Gunung Lawu dengan tugu trigulasi", 6);
        });
        initJalur(lawu, "via Cemoro Sewu", "Sedang", LevelFisik.MENENGAH, 7.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Cemoro Sewu Magetan", -7.660, 111.190, "Jalur tangga batu rapi paling ramai dan banyak warung", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Watu Gedeg", -7.645, 111.192, "Pos peristirahatan dengan shelter tertutup", 2);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Sendang Drajat", -7.630, 111.192, "Mata air sakral di dekat puncak Hargo Dumilah", 3);
            seedTitik(j, JenisTitik.POS, "Puncak Hargo Dumilah", -7.625, 111.192, "Tugu puncak tertinggi 3.265 mdpl", 4);
        });
        initJalur(lawu, "via Cemoro Kandang", "Sedang", LevelFisik.MENENGAH, 8.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Cemoro Kandang Karanganyar", -7.662, 111.185, "Jalur tanah landai berliku di lereng barat", 1);
            seedTitik(j, JenisTitik.POS, "Pos 3 Penggik", -7.640, 111.187, "Shelter peristirahatan hutan pinus dan cemara", 2);
            seedTitik(j, JenisTitik.POS, "Puncak Hargo Dumilah", -7.625, 111.192, "Puncak Lawu", 3);
        });

        // 14. Gunung Sindoro (3.153 mdpl) - Jawa Tengah
        Gunung sindoro = getOrCreateGunung("Gunung Sindoro", "Wonosobo & Temanggung, Jawa Tengah", 3153, -7.3000, 109.9980, "0813-9988-7766 (Basecamp Kledung)", "33.23.09.2001");
        initJalur(sindoro, "via Kledung", "Sedang", LevelFisik.MENENGAH, 6.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Kledung", -7.335, 110.015, "Basecamp di perbatasan Temanggung-Wonosobo", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Makam", -7.320, 110.008, "Batas kebun tembakau dan hutan pinus", 2);
            seedTitik(j, JenisTitik.POS, "Pos 3 Sunrise Camp", -7.310, 110.002, "Tempat camping favorit menghadap gagahnya Gunung Sumbing", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Puncak Kawah Sindoro", -7.300, 109.998, "Kawah aktif belerang dengan padang batu eksotis", 4);
        });
        initJalur(sindoro, "via Sigedang (Tambi)", "Sedang", LevelFisik.MENENGAH, 6.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Sigedang Kejajar", -7.265, 109.980, "Jalur lereng utara menyusuri perkebunan teh Tambi Wonosobo", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Watu Susun", -7.280, 109.988, "Bebatuan alam bertingkat dengan pemandangan Dieng", 2);
            seedTitik(j, JenisTitik.POS, "Kawah Utama Sindoro", -7.300, 109.998, "Bibir kawah utara Sindoro", 3);
        });
        initJalur(sindoro, "via Ndoro Arum (Banaran)", "Sedang-Sulit", LevelFisik.MENENGAH, 6.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Ndoro Arum Banaran", -7.325, 109.970, "Jalur barat daya yang asri dan sejuk", 1);
            seedTitik(j, JenisTitik.POS, "Lembah Bidadari", -7.312, 109.982, "Padang bunga edelweis dan bukit savana", 2);
        });

        // 15. Gunung Arjuno (3.339 mdpl) - Jawa Timur
        Gunung arjuno = getOrCreateGunung("Gunung Arjuno", "Malang-Pasuruan, Jawa Timur", 3339, -7.7610, 112.5960, "0812-1122-3344 (Basecamp Tretes)", "35.07.22.2001");
        initJalur(arjuno, "Lawang via Tretes", "Sedang", LevelFisik.MENENGAH, 8.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Tretes", -7.720, 112.620, "Basecamp utama pendakian Arjuno dari Tretes", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Lalijiwo", -7.740, 112.610, "Pos peristirahatan, vegetasi hutan montane", 2);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Sumber Air Lalijiwo", -7.742, 112.608, "Sumber air bersih di area Lalijiwo", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Angin Kencang Puncak Ogal-Agil", -7.761, 112.596, "Area puncak rawan angin kencang dan sambaran petir", 4);
        });
        initJalur(arjuno, "via Sumber Brantas (Batu)", "Sedang", LevelFisik.MENENGAH, 7.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Pos Jurang Kuali Sumber Brantas", -7.765, 112.540, "Pintu masuk lereng barat Kota Batu menyusuri kebun apel", 1);
            seedTitik(j, JenisTitik.POS, "Lembah Lengkehan", -7.762, 112.570, "Lembah pelana antara Gunung Arjuno dan Welirang", 2);
            seedTitik(j, JenisTitik.POS, "Pasar Setan Arjuno", -7.761, 112.585, "Dataran batu luas sebelum tanjakan Puncak Ogal-Agil", 3);
        });
        initJalur(arjuno, "via Purwosari (Candi)", "Sedang-Sulit", LevelFisik.MENENGAH, 8.5, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Purwosari Pasuruan", -7.770, 112.665, "Jalur sarat sejarah peninggalan situs pertapaan Majapahit", 1);
            seedTitik(j, JenisTitik.POS, "Situs Candi Sepilar", -7.765, 112.635, "Kompleks candi batu peninggalan kerajaan kuno", 2);
            seedTitik(j, JenisTitik.POS, "Puncak Ogal-Agil", -7.761, 112.596, "Puncak tertinggi Arjuno 3.339 mdpl", 3);
        });

        // 16. Gunung Welirang (3.156 mdpl) - Jawa Timur
        Gunung welirang = getOrCreateGunung("Gunung Welirang", "Pasuruan, Jawa Timur", 3156, -7.7660, 112.5770, "0812-1122-3344 (Basecamp Tretes)", "35.14.04.2005");
        initJalur(welirang, "via Tretes", "Mudah-Sedang", LevelFisik.PEMULA, 6.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Tretes", -7.720, 112.620, "Titik awal pendakian Welirang via Tretes", 1);
            seedTitik(j, JenisTitik.POS, "Pos 1 Air Panas", -7.735, 112.605, "Pos dekat sumber air panas alami", 2);
            seedTitik(j, JenisTitik.SUMBER_AIR, "Sumber Air Air Panas", -7.736, 112.604, "Sumber air panas belerang untuk rileks", 3);
            seedTitik(j, JenisTitik.TITIK_RAWAN, "Kawah Belerang Welirang", -7.766, 112.577, "Area kawah aktif tambang belerang, kenakan masker", 4);
        });
        initJalur(welirang, "via Pacet (Mojokerto)", "Sedang", LevelFisik.MENENGAH, 7.0, j -> {
            seedTitik(j, JenisTitik.BASECAMP, "Basecamp Claket Pacet", -7.685, 112.550, "Jalur lereng barat laut Mojokerto menyusuri hutan bambu", 1);
            seedTitik(j, JenisTitik.POS, "Pos 2 Putuk Lesung", -7.720, 112.560, "Pos peristirahatan hutan rimba", 2);
            seedTitik(j, JenisTitik.POS, "Kawah Jero Welirang", -7.760, 112.575, "Bibir kawah utara Welirang", 3);
        });

        log.info("  ✔ {} master gunung aktif", gunungRepository.count());
        log.info("  ✔ {} jalur pendakian aktif", jalurRepository.count());
        log.info("  ✔ {} titik pos waypoint aktif", titikJalurRepository.count());
    }

    private Gunung getOrCreateGunung(String nama, String lokasi, int mdpl, double lat, double lon, String kontak, String kodeWilayah) {
        return gunungRepository.findByNamaIgnoreCase(nama).orElseGet(() ->
                gunungRepository.save(Gunung.builder()
                        .nama(nama)
                        .lokasi(lokasi)
                        .ketinggianMdpl(mdpl)
                        .latitude(lat)
                        .longitude(lon)
                        .kontakBasecamp(kontak)
                        .kodeWilayah(kodeWilayah)
                        .build())
        );
    }

    private void initJalur(Gunung gunung, String namaJalur, String kesulitan, LevelFisik level, double estimasiJam, java.util.function.Consumer<Jalur> pointSeeder) {
        Jalur jalur = jalurRepository.findByGunungIdAndNamaJalurIgnoreCase(gunung.getId(), namaJalur).orElseGet(() ->
                jalurRepository.save(Jalur.builder()
                        .gunung(gunung)
                        .namaJalur(namaJalur)
                        .tingkatKesulitan(kesulitan)
                        .levelMinimum(level)
                        .estimasiJam(estimasiJam)
                        .build())
        );

        if (!titikJalurRepository.existsByJalurId(jalur.getId())) {
            pointSeeder.accept(jalur);
        }
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

    // -------------------------------------------------------------------------
    // 3. Rencana Pendakian & Peserta
    // -------------------------------------------------------------------------
    private void seedRencanaDanPeserta() {
        if (rencanaRepository.count() > 0) {
            // Perbarui data rencana yang sudah ada jika detail pertemuan masih kosong
            for (RencanaPendakian r : rencanaRepository.findAll()) {
                if (r.getWaktuKumpul() == null || r.getTitikKumpul() == null) {
                    if (r.getCatatan() != null && r.getCatatan().contains("Prau")) {
                        r.setWaktuKumpul("06:30 WIB");
                        r.setTitikKumpul("Basecamp Patak Banteng (Wonosobo)");
                        r.setPerlengkapan("Tenda dome, Sleeping bag polar, Matras foil, Jaket hangat, Jas hujan, Headlamp, Logistik pribadi");
                        r.setEstimasiBiaya(65000);
                        r.setKontak("0812-3456-7890");
                    } else if (r.getCatatan() != null && r.getCatatan().contains("Rinjani")) {
                        r.setWaktuKumpul("05:30 WITA");
                        r.setTitikKumpul("Basecamp TNGR Sembalun (Lombok Timur)");
                        r.setPerlengkapan("Tenda windproof 4 season, Sleeping bag bulu angsa, Matras tebal, Jaket windproof/down jacket, Headlamp & baterai cadangan, Sepatu trekking, Logistik 4 hari, P3K pribadi");
                        r.setEstimasiBiaya(350000);
                        r.setKontak("0852-6677-8899");
                    } else if (r.getCatatan() != null && r.getCatatan().contains("Papandayan")) {
                        r.setWaktuKumpul("07:00 WIB");
                        r.setTitikKumpul("Pos Tiket Camp David Cisurupan (Garut)");
                        r.setPerlengkapan("Tenda, Sleeping bag, Matras, Jaket gunung, Jas hujan, Pakaian ganti, Air minum min 2L, Makanan ringan");
                        r.setEstimasiBiaya(50000);
                        r.setKontak("0813-2012-3456");
                    } else {
                        r.setWaktuKumpul("06:00 WIB");
                        r.setTitikKumpul("Basecamp Utama");
                        r.setPerlengkapan("Tenda, Sleeping bag, Matras, Jaket gunung, Jas hujan, Headlamp");
                        r.setEstimasiBiaya(75000);
                        r.setKontak("0812-3456-7890");
                    }
                    rencanaRepository.save(r);
                }
            }
            return;
        }

        Pengguna budi = penggunaRepository.findByEmail("budi@example.com").orElse(null);
        Pengguna sari = penggunaRepository.findByEmail("sari@example.com").orElse(null);
        Pengguna andi = penggunaRepository.findByEmail("andi@example.com").orElse(null);

        if (budi == null || sari == null || andi == null) {
            return;
        }

        jalurRepository.findAll().stream().filter(j -> j.getNamaJalur().contains("Patak Banteng")).findFirst().ifPresent(jalurPrau -> {
            RencanaPendakian rencanaPrau = rencanaRepository.save(RencanaPendakian.builder()
                    .pembuat(sari)
                    .jalur(jalurPrau)
                    .tanggalNaik(LocalDate.now().plusDays(7))
                    .waktuKumpul("06:30 WIB")
                    .titikKumpul("Basecamp Patak Banteng (Wonosobo)")
                    .perlengkapan("Tenda dome, Sleeping bag polar, Matras foil, Jaket hangat, Jas hujan, Headlamp, Logistik pribadi")
                    .estimasiBiaya(65000)
                    .kontak("0812-3456-7890")
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
                    .waktuKumpul("05:30 WITA")
                    .titikKumpul("Basecamp TNGR Sembalun (Lombok Timur)")
                    .perlengkapan("Tenda windproof 4 season, Sleeping bag bulu angsa, Matras tebal, Jaket windproof/down jacket, Headlamp & baterai cadangan, Sepatu trekking, Logistik 4 hari, P3K pribadi")
                    .estimasiBiaya(350000)
                    .kontak("0852-6677-8899")
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
                    .waktuKumpul("07:00 WIB")
                    .titikKumpul("Pos Tiket Camp David Cisurupan (Garut)")
                    .perlengkapan("Tenda, Sleeping bag, Matras, Jaket gunung, Jas hujan, Pakaian ganti, Air minum min 2L, Makanan ringan")
                    .estimasiBiaya(50000)
                    .kontak("0813-2012-3456")
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
}
