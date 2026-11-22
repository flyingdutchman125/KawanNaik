-- =============================================================================
-- KawanNaik — Hiking Buddy Finder
-- Database Schema & Seed Data for MySQL 8.x
-- =============================================================================

CREATE DATABASE IF NOT EXISTS kawannaik
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE kawannaik;

-- -----------------------------------------------------------------------------
-- Drop tables in reverse-dependency order
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS peserta_rencana;
DROP TABLE IF EXISTS rencana_pendakian;
DROP TABLE IF EXISTS titik_jalur;
DROP TABLE IF EXISTS jalur;
DROP TABLE IF EXISTS gunung;
DROP TABLE IF EXISTS panduan_keselamatan;
DROP TABLE IF EXISTS pengguna;

-- =============================================================================
-- 1. pengguna — registered users (hikers & admins)
-- =============================================================================
CREATE TABLE pengguna (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nama           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    peran          ENUM('PENDAKI','ADMIN') NOT NULL DEFAULT 'PENDAKI',
    level_fisik    ENUM('PEMULA','MENENGAH','MAHIR') NOT NULL DEFAULT 'PEMULA',
    pengalaman     TEXT,
    kontak_darurat VARCHAR(255),
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pengguna_email (email)
) ENGINE=InnoDB;

-- =============================================================================
-- 2. gunung — mountains
-- =============================================================================
CREATE TABLE gunung (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nama            VARCHAR(100) NOT NULL,
    lokasi          VARCHAR(255) NOT NULL,
    ketinggian_mdpl INT          NOT NULL,
    latitude        DOUBLE,
    longitude       DOUBLE,
    kontak_basecamp VARCHAR(50),
    kode_wilayah    VARCHAR(20),
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- =============================================================================
-- 3. jalur — hiking trails (belongs to a mountain)
-- =============================================================================
CREATE TABLE jalur (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    gunung_id         BIGINT       NOT NULL,
    nama_jalur        VARCHAR(100) NOT NULL,
    tingkat_kesulitan VARCHAR(50),
    level_minimum     ENUM('PEMULA','MENENGAH','MAHIR') NOT NULL,
    estimasi_jam      DOUBLE,
    gpx_data          TEXT,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_jalur_gunung FOREIGN KEY (gunung_id) REFERENCES gunung(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================================================
-- 4. titik_jalur — waypoints along a trail
-- =============================================================================
CREATE TABLE titik_jalur (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    jalur_id   BIGINT       NOT NULL,
    jenis      ENUM('BASECAMP','POS','SUMBER_AIR','TITIK_RAWAN') NOT NULL,
    nama       VARCHAR(100) NOT NULL,
    latitude   DOUBLE,
    longitude  DOUBLE,
    deskripsi  TEXT,
    urutan     INT          NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_titik_jalur FOREIGN KEY (jalur_id) REFERENCES jalur(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================================================
-- 5. rencana_pendakian — hiking plans
-- =============================================================================
CREATE TABLE rencana_pendakian (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    pembuat_id    BIGINT NOT NULL,
    jalur_id      BIGINT NOT NULL,
    tanggal_naik  DATE   NOT NULL,
    kuota         INT    NOT NULL,
    level_minimum ENUM('PEMULA','MENENGAH','MAHIR'),
    catatan       TEXT,
    status        ENUM('DIBUKA','DITUTUP','SELESAI','DIBATALKAN') DEFAULT 'DIBUKA',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_rencana_pembuat FOREIGN KEY (pembuat_id) REFERENCES pengguna(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_rencana_jalur   FOREIGN KEY (jalur_id)   REFERENCES jalur(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================================================
-- 6. peserta_rencana — join requests for a hiking plan
-- =============================================================================
CREATE TABLE peserta_rencana (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    rencana_id   BIGINT NOT NULL,
    pengguna_id  BIGINT NOT NULL,
    status       ENUM('MENUNGGU','DITERIMA','DITOLAK') DEFAULT 'MENUNGGU',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_peserta_rencana (rencana_id, pengguna_id),
    CONSTRAINT fk_peserta_rencana  FOREIGN KEY (rencana_id)  REFERENCES rencana_pendakian(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_peserta_pengguna FOREIGN KEY (pengguna_id) REFERENCES pengguna(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================================================
-- 7. panduan_keselamatan — safety guides
-- =============================================================================
CREATE TABLE panduan_keselamatan (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    judul      VARCHAR(200) NOT NULL,
    isi        TEXT         NOT NULL,
    kategori   VARCHAR(100),
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;


-- =============================================================================
--  SEED DATA
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Pengguna (password = 'password123', BCrypt hash)
-- -----------------------------------------------------------------------------
INSERT INTO pengguna (nama, email, password_hash, peran, level_fisik, pengalaman, kontak_darurat) VALUES
('Admin KawanNaik', 'admin@kawannaik.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'ADMIN', 'MAHIR', 'Berpengalaman mendaki sejak 2010 dan pengelola jalur', '08001234567'),

('Budi Santoso', 'budi@example.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'PENDAKI', 'MAHIR', 'Sudah mendaki 20+ gunung di Jawa, Sumatera, dan NTB', '08111111111'),

('Sari Wulandari', 'sari@example.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'PENDAKI', 'MENENGAH', 'Hobi hiking sejak kuliah, 10 gunung di Jawa', '08122222222'),

('Andi Pratama', 'andi@example.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'PENDAKI', 'PEMULA', 'Baru mulai mendaki, antusias belajar teknik mountaineering', '08133333333');

-- -----------------------------------------------------------------------------
-- Gunung (Favorit Pemula s.d. Senior / Mahir)
-- -----------------------------------------------------------------------------
INSERT INTO gunung (id, nama, lokasi, ketinggian_mdpl, latitude, longitude, kontak_basecamp, kode_wilayah) VALUES
(1,  'Gunung Semeru',     'Lumajang, Jawa Timur',                    3676, -8.1080, 112.9220, '0812-3456-7890 (TNBTS Ranu Pani)',       '35.08.05.2002'),
(2,  'Gunung Rinjani',    'Lombok Timur, Nusa Tenggara Barat',       3726, -8.4190, 116.4580, '0819-0700-1234 (TNGR Sembalun)',         '52.03.09.2001'),
(3,  'Gunung Kerinci',    'Kerinci, Jambi & Solok Selatan, Sumbar',  3805, -1.6970, 101.2640, '0852-6677-8899 (Pos R10 Kersik Tuo)',     '15.01.05.2001'),
(4,  'Gunung Raung',      'Banyuwangi-Bondowoso, Jawa Timur',        3344, -8.1250, 114.0450, '0812-4980-9988 (Basecamp Kalibaru)',       '35.10.02.2001'),
(5,  'Gunung Slamet',     'Purbalingga, Jawa Tengah',                3428, -7.2420, 109.2080, '0857-1234-5678 (Basecamp Bambangan)',     '33.03.10.2001'),
(6,  'Gunung Prau',       'Wonosobo & Batang, Jawa Tengah',          2565, -7.1856, 109.9234, '0852-2891-2345 (Basecamp Patak Banteng)', '33.07.12.2001'),
(7,  'Gunung Papandayan', 'Garut, Jawa Barat',                       2665, -7.3200, 107.7300, '0813-2012-3456 (Pos Tiket Camp David)',    '32.05.08.2001'),
(8,  'Gunung Andong',     'Magelang, Jawa Tengah',                   1726, -7.3889, 110.3667, '0812-3456-7891 (Basecamp Sawit)',          '33.08.17.2001'),
(9,  'Gunung Ijen',       'Banyuwangi-Bondowoso, Jawa Timur',        2769, -8.0580, 114.2420, '0819-8765-4321 (TWA Kawah Ijen Paltuding)', '35.10.15.2001'),
(10, 'Gunung Batur',      'Kintamani, Bangli, Bali',                 1717, -8.2420, 115.3750, '0878-6543-2109 (Basecamp Toya Bungkah)',   '51.06.04.2001'),
(11, 'Gunung Merbabu',    'Boyolali & Magelang, Jawa Tengah',        3145, -7.4550, 110.4400, '0821-3344-5566 (Basecamp Selo Baru)',     '33.09.11.2001'),
(12, 'Gunung Gede',       'Cianjur, Sukabumi & Bogor, Jawa Barat',   2958, -6.7830, 106.9830, '0811-2233-4455 (Balai TNGGP Cibodas)',     '32.03.02.2001'),
(13, 'Gunung Lawu',       'Karanganyar (Jateng) & Magetan (Jatim)',  3265, -7.6250, 111.1920, '0822-4455-6677 (Basecamp Candi Cetho)',    '33.13.14.2001'),
(14, 'Gunung Sindoro',    'Wonosobo & Temanggung, Jawa Tengah',      3153, -7.3000, 109.9980, '0813-9988-7766 (Basecamp Kledung)',        '33.23.09.2001'),
(15, 'Gunung Arjuno',     'Malang-Pasuruan, Jawa Timur',             3339, -7.7610, 112.5960, '0812-1122-3344 (Basecamp Tretes)',         '35.07.22.2001'),
(16, 'Gunung Welirang',   'Pasuruan, Jawa Timur',                    3156, -7.7660, 112.5770, '0812-1122-3344 (Basecamp Tretes)',         '35.14.04.2005');

-- -----------------------------------------------------------------------------
-- Jalur
-- -----------------------------------------------------------------------------
INSERT INTO jalur (id, gunung_id, nama_jalur, tingkat_kesulitan, level_minimum, estimasi_jam) VALUES
(1,  1,  'Ranu Pani',                      'Sulit',                 'MAHIR',    12),
(2,  1,  'Arek-Arek',                      'Sangat Sulit',          'MAHIR',    14),
(3,  1,  'via Tawon Songo',                'Ekstrem / Petualangan', 'MAHIR',    15),
(4,  2,  'via Sembalun',                   'Sangat Sulit',          'MAHIR',    14),
(5,  2,  'via Senaru',                     'Sulit',                 'MAHIR',    13),
(6,  2,  'via Torean',                     'Sulit / Petualangan',   'MAHIR',    12),
(7,  2,  'via Aik Berik',                  'Sedang-Sulit',          'MENENGAH', 11),
(8,  3,  'via Kersik Tuo',                 'Sangat Sulit',          'MAHIR',    12),
(9,  3,  'via Solok Selatan (Bangun Rejo)','Ekstrem / Ekspedisi',   'MAHIR',    18),
(10, 4,  'via Kalibaru',                   'Ekstrem / Teknikal',    'MAHIR',    16),
(11, 4,  'via Sumberwringin',              'Sulit',                 'MAHIR',    12),
(12, 5,  'via Bambangan',                  'Sangat Sulit',          'MAHIR',    11),
(13, 5,  'via Guci (Tegal)',               'Sulit',                 'MAHIR',    12),
(14, 5,  'via Dipajaya (Pemalang)',        'Sulit',                 'MAHIR',    11.5),
(15, 5,  'via Baturraden (Banyumas)',      'Sangat Sulit',          'MAHIR',    13),
(16, 6,  'via Patak Banteng',              'Mudah',                 'PEMULA',   3.5),
(17, 6,  'via Dieng Kulon',                'Mudah',                 'PEMULA',   4.0),
(18, 6,  'via Kalilembu',                  'Mudah',                 'PEMULA',   3.5),
(19, 6,  'via Wates (Temanggung)',         'Mudah-Sedang',          'PEMULA',   4.5),
(20, 7,  'via Camp David',                 'Mudah',                 'PEMULA',   4.0),
(21, 7,  'via Cisurupan',                  'Mudah-Sedang',          'PEMULA',   4.5),
(22, 8,  'via Sawit',                      'Mudah',                 'PEMULA',   2.0),
(23, 8,  'via Pendem',                     'Mudah',                 'PEMULA',   2.0),
(24, 8,  'via Gogik',                      'Mudah',                 'PEMULA',   2.0),
(25, 9,  'via Paltuding',                  'Mudah-Sedang',          'PEMULA',   2.5),
(26, 9,  'via Taman Dadar / Rembangan',    'Sedang',                'MENENGAH', 5.0),
(27, 10, 'via Toya Bungkah',               'Mudah',                 'PEMULA',   2.5),
(28, 10, 'via Pasar Agung',                'Mudah',                 'PEMULA',   2.0),
(29, 11, 'via Selo',                       'Sedang',                'MENENGAH', 7.0),
(30, 11, 'via Suwanting',                  'Sulit',                 'MAHIR',    9.0),
(31, 11, 'via Wekas',                      'Sedang',                'MENENGAH', 7.0),
(32, 11, 'via Thekelan',                   'Sedang-Sulit',          'MENENGAH', 8.0),
(33, 12, 'via Cibodas',                    'Sedang',                'MENENGAH', 7.5),
(34, 12, 'via Gunung Putri',               'Sedang-Sulit',          'MENENGAH', 6.0),
(35, 12, 'via Selabintana',                'Sulit',                 'MAHIR',    10.0),
(36, 13, 'via Candi Cetho',                'Sedang',                'MENENGAH', 8.5),
(37, 13, 'via Cemoro Sewu',                'Sedang',                'MENENGAH', 7.0),
(38, 13, 'via Cemoro Kandang',             'Sedang',                'MENENGAH', 8.0),
(39, 14, 'via Kledung',                    'Sedang',                'MENENGAH', 6.5),
(40, 14, 'via Sigedang (Tambi)',           'Sedang',                'MENENGAH', 6.0),
(41, 14, 'via Ndoro Arum (Banaran)',       'Sedang-Sulit',          'MENENGAH', 6.5),
(42, 15, 'Lawang via Tretes',              'Sedang',                'MENENGAH', 8.0),
(43, 15, 'via Sumber Brantas (Batu)',      'Sedang',                'MENENGAH', 7.5),
(44, 15, 'via Purwosari (Candi)',          'Sedang-Sulit',          'MENENGAH', 8.5),
(45, 16, 'via Tretes',                     'Mudah-Sedang',          'PEMULA',   6.0),
(46, 16, 'via Pacet (Mojokerto)',          'Sedang',                'MENENGAH', 7.0);

-- -----------------------------------------------------------------------------
-- Titik Jalur
-- -----------------------------------------------------------------------------
INSERT INTO titik_jalur (jalur_id, jenis, nama, latitude, longitude, deskripsi, urutan) VALUES
-- Semeru Ranu Pani
(1, 'BASECAMP',    'Basecamp Ranu Pani',     -8.013, 112.952, 'Titik awal pendakian, registrasi & briefing TNBTS', 1),
(1, 'POS',         'Pos 1 Watu Rejeng',      -8.030, 112.940, 'Pos peristirahatan pertama di bawah tebing batu', 2),
(1, 'POS',         'Ranu Kumbolo',           -8.050, 112.932, 'Danau air tawar indah, area mendirikan tenda utama', 3),
(1, 'TITIK_RAWAN', 'Tanjakan Cinta',         -8.053, 112.933, 'Tanjakan terjal melegenda, rawan kram otot', 4),
(1, 'POS',         'Pos Kalimati',           -8.065, 112.930, 'Camp terakhir sebelum summit attack Mahameru', 5),
(1, 'SUMBER_AIR',  'Sumber Mani',            -8.066, 112.931, 'Mata air alami bersih di hutan dekat Kalimati', 6),
(1, 'TITIK_RAWAN', 'Puncak Mahameru',        -8.108, 112.922, 'Tanjakan pasir curam, waspadai gas beracun & batu gugur', 7),

-- Rinjani via Sembalun
(4, 'BASECAMP',    'Basecamp Sembalun',      -8.358, 116.531, 'Pintu gerbang registrasi pendakian Rinjani', 1),
(4, 'POS',         'Pos 1 Pemantauan',       -8.375, 116.515, 'Pos istirahat di tengah padang savana Sembalun', 2),
(4, 'POS',         'Pos 2 Tengengean',       -8.390, 116.495, 'Pos peristirahatan dengan jembatan sungai kecil', 3),
(4, 'POS',         'Pos 3 Pada Balong',      -8.402, 116.479, 'Pos terakhir sebelum tanjakan ekstrem Bukit Penyesalan', 4),
(4, 'TITIK_RAWAN', 'Bukit Penyesalan',       -8.410, 116.468, '7 bukit berturut-turut dengan tanjakan sangat terjal', 5),
(4, 'POS',         'Plawangan Sembalun',     -8.416, 116.460, 'Bibir kawah dengan pemandangan Danau Segara Anak & Gunung Barujari', 6),
(4, 'SUMBER_AIR',  'Mata Air Plawangan',     -8.417, 116.459, 'Sumber mata air di tebing dekat Plawangan', 7),
(4, 'TITIK_RAWAN', 'Letter E Summit Ridge',  -8.418, 116.458, 'Punggungan sempit berpasir licin menuju Puncak Dewi Anjani', 8),

-- Kerinci via Kersik Tuo
(8, 'BASECAMP',    'Pintu Rimba R10',        -1.745, 101.265, 'Batas perkebunan teh Kayu Aro dan hutan tropis TNKS', 1),
(8, 'POS',         'Pos 1 Bangku Panjang',   -1.735, 101.264, 'Pos peristirahatan awal', 2),
(8, 'POS',         'Pos 3 Pondok Panorama',  -1.720, 101.263, 'Panorama kebun teh dan perbukitan Kerinci', 3),
(8, 'POS',         'Shelter 1 & 2',          -1.710, 101.264, 'Area perkemahan di dalam hutan lebat', 4),
(8, 'TITIK_RAWAN', 'Lorong Hantu',           -1.705, 101.264, 'Jalur lorong tanah terjal dengan akar pohon menjuntai', 5),
(8, 'POS',         'Shelter 3',              -1.702, 101.264, 'Camp tertinggi sebelum summit attack (3.073 mdpl)', 6),
(8, 'SUMBER_AIR',  'Mata Air Shelter 3',     -1.703, 101.265, 'Mata air rembesan jurang dekat Shelter 3', 7),
(8, 'TITIK_RAWAN', 'Puncak Indrapura',       -1.697, 101.264, 'Atap Sumatera 3.805 mdpl, medan cadas terjal dan bebatuan', 8),

-- Raung via Kalibaru
(10, 'BASECAMP',   'Basecamp Kalibaru',      -8.210, 113.985, 'Registrasi dan pengecekan perlengkapan climbing / tali temali', 1),
(10, 'POS',        'Pos 1 Pak Syarif',       -8.190, 114.005, 'Pondok singgah perkebunan kopi', 2),
(10, 'POS',        'Pos 4 Pondok Motor',     -8.160, 114.020, 'Pos peristirahatan hutan vegetasi pinus', 3),
(10, 'POS',        'Pos 7 Camp Terakhir',    -8.138, 114.038, 'Lokasi perkemahan terakhir sebelum summit attack', 4),
(10, 'POS',        'Puncak Bendera',         -8.130, 114.040, 'Titik persiapan pemakaian alat safety harness & carabiner', 5),
(10, 'TITIK_RAWAN','Jembatan Shiratal Mustaqim', -8.127, 114.043, 'Punggungan batu selebar 1 meter dengan jurang 500m di kedua sisi', 6),
(10, 'TITIK_RAWAN','Puncak Sejati Raung',    -8.125, 114.045, 'Puncak sejati dengan pemandangan kaldera raksasa menakjubkan', 7),

-- Slamet via Bambangan
(12, 'BASECAMP',   'Basecamp Bambangan',     -7.275, 109.245, 'Basecamp resmi terpopuler pendakian Atap Jawa Tengah', 1),
(12, 'POS',        'Pos 1 Pondok Gembirung', -7.265, 109.235, 'Pos dengan warung dan shelter berteduh', 2),
(12, 'POS',        'Pos 3 Pondok Cemara',    -7.255, 109.225, 'Hutan cemara asri dan tanah lapang camp', 3),
(12, 'SUMBER_AIR', 'Pos 5 Samyang Rangkah',  -7.248, 109.218, 'Sumber mata air utama para pendaki', 4),
(12, 'POS',        'Pos 9 Plawangan',        -7.245, 109.210, 'Batas vegetasi terakhir sebelum zona bebatuan', 5),
(12, 'TITIK_RAWAN','Puncak Surono',          -7.242, 109.208, 'Medan pasir dan batu merah labil yang curam, rawan badai es', 6),

-- Prau via Patak Banteng
(16, 'BASECAMP',   'Basecamp Patak Banteng', -7.208, 109.932, 'Basecamp favorit pemula dengan fasilitas komplit & ojek', 1),
(16, 'POS',        'Pos 1 Senthongsari',     -7.200, 109.930, 'Pos pertama dengan gazebo dan warung makanan', 2),
(16, 'POS',        'Pos 2 Canggal Walitis',  -7.195, 109.928, 'Jalur tangga batu di tengah perkebunan dan pinus', 3),
(16, 'TITIK_RAWAN','Pos 3 Cacingan',         -7.190, 109.925, 'Tanjakan tangga tanah berakar, licin saat musim hujan', 4),
(16, 'POS',        'Sunrise Camp Teletubbies', -7.186, 109.923, 'Puncak hamparan bukit rumput luas, Golden Sunrise terindah', 5),

-- Papandayan via Camp David
(20, 'BASECAMP',   'Basecamp Camp David',    -7.315, 107.742, 'Pintu gerbang wisata pendakian ramah keluarga & pemula', 1),
(20, 'TITIK_RAWAN','Kawah Mas Belerang',     -7.320, 107.738, 'Asap belerang aktif, wajib masker saat melintas', 2),
(20, 'POS',        'Pondok Saladah',         -7.325, 107.732, 'Area camping favorit berumput dengan toilet dan warung', 3),
(20, 'SUMBER_AIR', 'Sungai Pondok Saladah',  -7.326, 107.731, 'Aliran air jernih pegunungan untuk masak dan minum', 4),
(20, 'POS',        'Hutan Mati Papandayan',  -7.328, 107.728, 'Pohon cantigi hangus eksotis akibat letusan masa lampau', 5),
(20, 'POS',        'Tegal Alun',             -7.332, 107.725, 'Lembah tersembunyi dengan ribuan bunga edelweis abadi', 6),

-- Andong via Sawit
(22, 'BASECAMP',   'Basecamp Sawit',         -7.395, 110.370, 'Basecamp ramah pendaki pemula, jalur tangga tertata rapi', 1),
(22, 'POS',        'Pos 1 Kemuning',         -7.392, 110.368, 'Hutan pinus teduh dan sejuk', 2),
(22, 'POS',        'Pos 2 Dewa Daru',        -7.390, 110.367, 'Warung kopi dan istirahat sebelum punggungan puncak', 3),
(22, 'POS',        'Puncak Makam & Alap-Alap', -7.388, 110.366, 'Puncak 360 derajat melihat Merbabu, Merapi, Sindoro, dan Sumbing', 4),

-- Ijen via Paltuding
(25, 'BASECAMP',   'Pos Paltuding',          -8.075, 114.225, 'Pintu masuk resmi TWA Ijen, fasilitas lengkap dan persewaan masker gas', 1),
(25, 'POS',        'Pos Bunder',             -8.065, 114.235, 'Pos timbang belerang para penambang tradisional', 2),
(25, 'POS',        'Bibir Kawah Ijen',       -8.058, 114.242, 'Pemandangan danau asam toska terbesar di dunia', 3),
(25, 'TITIK_RAWAN','Dasar Kawah Blue Fire',  -8.056, 114.245, 'Fenomena api biru dunia, wajib masker gas respirator', 4),

-- Merbabu via Selo
(29, 'BASECAMP',   'Basecamp Selo',          -7.495, 110.470, 'Pintu masuk jalur favorit sejuta umat di lereng timur', 1),
(29, 'POS',        'Pos 1 Dok-dok',          -7.485, 110.465, 'Pos peristirahatan awal', 2),
(29, 'POS',        'Pos 3 Batu Tulis',       -7.470, 110.455, 'Area camp dengan pemandangan megah Gunung Merapi di seberang', 3),
(29, 'POS',        'Sabana 1 & Sabana 2',    -7.460, 110.445, 'Hamparan bukit rumput savana hijau bergelombang yang memukau', 4),
(29, 'TITIK_RAWAN','Puncak Kenteng Songo',   -7.455, 110.440, 'Puncak tertinggi Merbabu dengan batu lesung bersejarah', 5),

-- Gede via Cibodas
(33, 'BASECAMP',   'Pintu Masuk Cibodas',    -6.745, 107.005, 'Pintu gerbang TNGGP, hutan hujan tropis basah', 1),
(33, 'POS',        'Telaga Biru & Gayonggong', -6.755, 106.995, 'Danau alami dan jembatan kayu di atas rawa', 2),
(33, 'TITIK_RAWAN','Air Panas Cibeureum',    -6.765, 106.990, 'Tebing batu basah dengan aliran air panas belerang alami', 3),
(33, 'POS',        'Kandang Badak',          -6.775, 106.985, 'Pos persimpangan Gede-Pangrango, area camp utama', 4),
(33, 'SUMBER_AIR', 'Mata Air Kandang Badak', -6.776, 106.984, 'Sumber air melimpah di dekat shelter pos', 5),
(33, 'POS',        'Alun-Alun Suryakencana', -6.790, 106.985, 'Padang edelweis legendaris seluas 50 hektar', 6),
(33, 'POS',        'Puncak Gede & Kawah Ratu', -6.783, 106.983, 'Puncak batu menghadap kawah aktif yang menakjubkan', 7),

-- Lawu via Candi Cetho
(36, 'BASECAMP',   'Basecamp Candi Cetho',   -7.595, 111.155, 'Titik awal di kawasan cagar budaya Candi Hindu Cetho', 1),
(36, 'POS',        'Pos 3 Penggik',          -7.608, 111.170, 'Pos peristirahatan hutan cemara', 2),
(36, 'POS',        'Pos 5 Bulak Peperangan', -7.618, 111.185, 'Sabana savana luas lokasi perkemahan populer', 3),
(36, 'POS',        'Gupak Menjangan & Pasar Dieng', -7.622, 111.189, 'Telaga kecil musiman dan dataran batu mistis', 4),
(36, 'POS',        'Warung Mbok Yem',        -7.624, 111.191, 'Warung tertinggi di Indonesia pada ketinggian 3.150 mdpl', 5),
(36, 'POS',        'Puncak Hargo Dumilah',   -7.625, 111.192, 'Puncak tertinggi Gunung Lawu dengan tugu trigulasi', 6);

-- -----------------------------------------------------------------------------
-- Rencana Pendakian — sample plans
-- -----------------------------------------------------------------------------
INSERT INTO rencana_pendakian (pembuat_id, jalur_id, tanggal_naik, kuota, level_minimum, catatan, status) VALUES
(2, 1,  '2026-11-15', 5,  'MAHIR',    'Pendakian Semeru via Ranu Pani. Wajib bawa sleeping bag dan jaket tebal. Kumpul di Malang H-1.', 'DIBUKA'),
(3, 16, '2026-11-20', 6,  'PEMULA',   'Hiking santai menikmati sunrise Gunung Prau Dieng. Terbuka untuk pemula. Kumpul di Basecamp Patak Banteng.', 'DIBUKA'),
(2, 4,  '2026-12-05', 4,  'MAHIR',    'Ekspedisi Rinjani 4D3N via Sembalun tembus Senaru. Wajib fisik prima & bawa perlengkapan standar summit dingin.', 'DIBUKA'),
(3, 20, '2026-11-28', 10, 'PEMULA',   'Camp ceria Papandayan di Pondok Saladah dan foto di Hutan Mati. Cocok untuk first-time hiker.', 'DIBUKA');

-- -----------------------------------------------------------------------------
-- Peserta Rencana — sample join requests
-- -----------------------------------------------------------------------------
INSERT INTO peserta_rencana (rencana_id, pengguna_id, status) VALUES
(1, 3, 'MENUNGGU'),
(1, 4, 'DITOLAK'),
(2, 4, 'DITERIMA'),
(3, 3, 'MENUNGGU'),
(4, 2, 'DITERIMA'),
(4, 4, 'DITERIMA');

-- -----------------------------------------------------------------------------
-- Panduan Keselamatan
-- -----------------------------------------------------------------------------
INSERT INTO panduan_keselamatan (judul, isi, kategori) VALUES
('Pencegahan dan Penanganan Hipotermia',
 'Hipotermia terjadi saat suhu inti tubuh turun di bawah 35°C. Langkah pencegahan:\n1. Sistem 3-Layer: Base layer (dry-fit sintetis/wol merino), mid-layer (fleece penghangat), outer layer (jaket windproof & waterproof).\n2. Jangan pernah memakai pakaian jeans/katun karena menyerap air dan lama kering.\n3. Selalu simpan pakaian ganti dan sleeping bag di dalam kantong plastik/dry bag di dalam ransel.\n4. Asup kalori tinggi dan minuman manis hangat secara teratur selama pendakian.\n5. Jika rekan mulai menggigil hebat atau bicaranya meracau: segera masukkan ke dalam sleeping bag di tenda kering, ganti pakaian basahnya, dan beri penghangat tubuh.',
 'Kesehatan'),

('Mengenali dan Mengatasi Acute Mountain Sickness (AMS)',
 'Acute Mountain Sickness (AMS) rentan menyerang pendaki di ketinggian > 2.500 mdpl (seperti Semeru, Slamet, Rinjani, Kerinci).\nGejala:\n- Sakit kepala berdenyut, mual, pusing, lemas berlebih, dan sesak nafas ringan.\nPencegahan:\n1. Lakukan aklimatisasi dengan bermalam di basecamp atau pos awal sebelum mendaki cepat.\n2. Climb high, sleep low: naik bertahap, jangan tergesa-gesa berlari mengejar waktu.\n3. Minum air putih minimal 3-4 liter per hari untuk menjaga sirkulasi oksigen darah.\nPenanganan:\n- HUKUM UTAMA: Jika gejala memberat atau muncul ataksia (jalan sempoyongan), SEGERA EVAKUASI TURUN KE KETINGGIAN LEBIH RENDAH! Jangan paksakan summit.',
 'Kesehatan'),

('Tips Navigasi Darat dan Orientasi Medan',
 'Tersesat di jalur adalah salah satu penyebab insiden terbanyak di gunung Indonesia.\n1. Pelajari kontur dan topografi gunung sebelum mendaki (unduh peta digital offline / GPX di KawanNaik).\n2. Bawa kompas bidik fisik dan peluit di tali ransel yang mudah dijangkau.\n3. Perhatikan percabangan jalur dan penanda jalan (pita warna, plang pos, cat di batu).\n4. Jangan pernah memotong jalur resmi (shortcut switchback) karena selain merusak vegetasi, berisiko jatuh ke jurang curam.\n5. Jika tersesat, terapkan protokol S-T-O-P (Sit, Think, Observe, Plan). Tetap di jalur terakhir yang dikenali.',
 'Navigasi'),

('Etika dan Prinsip Leave No Trace (LNT)',
 'Sebagai pendaki bertanggung jawab, hormati alam dan warga lokal:\n1. Sampahmu tanggung jawabmu — bawa turun SEMUA sampah termasuk puntung rokok dan tisu basah.\n2. Jangan memetik bunga edelweis atau merusak flora dan fauna langka.\n3. Buat api unggun hanya di tempat yang diizinkan dan pastikan padam total sebelum ditinggal.\n4. Hormati adat istiadat setempat dan kesakralan situs/makam di gunung.\n5. Buang air besar/kecil minimal 60 meter dari sumber mata air dan jalur pendakian (gali lubang sedalam 15-20 cm dan kubur kembali).',
 'Etika');
