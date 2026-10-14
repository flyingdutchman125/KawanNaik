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
    status        ENUM('DIBUKA','DITUTUP','SELESAI') DEFAULT 'DIBUKA',
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
 'ADMIN', 'MAHIR', 'Berpengalaman mendaki sejak 2010', '08001234567'),

('Budi Santoso', 'budi@example.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'PENDAKI', 'MAHIR', 'Sudah mendaki 20+ gunung di Jawa dan Sumatera', '08111111111'),

('Sari Wulandari', 'sari@example.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'PENDAKI', 'MENENGAH', 'Hobi hiking sejak kuliah, 10 gunung', '08122222222'),

('Andi Pratama', 'andi@example.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'PENDAKI', 'PEMULA', 'Baru mulai mendaki, antusias belajar', '08133333333');

-- -----------------------------------------------------------------------------
-- Gunung (Jawa Timur)
-- NOTE: Koordinat bersifat perkiraan dan perlu diverifikasi di lapangan.
-- -----------------------------------------------------------------------------
INSERT INTO gunung (nama, lokasi, ketinggian_mdpl, latitude, longitude) VALUES
('Gunung Semeru',   'Lumajang, Jawa Timur',         3676, -8.108,  112.922),
('Gunung Arjuno',   'Malang-Pasuruan, Jawa Timur',  3339, -7.761,  112.596),
('Gunung Welirang', 'Pasuruan, Jawa Timur',         3156, -7.766,  112.577);

-- -----------------------------------------------------------------------------
-- Jalur
-- NOTE: Koordinat titik jalur bersifat perkiraan dan perlu diverifikasi.
-- -----------------------------------------------------------------------------
-- Semeru — Ranu Pani (id=1)
INSERT INTO jalur (gunung_id, nama_jalur, tingkat_kesulitan, level_minimum, estimasi_jam) VALUES
(1, 'Ranu Pani',          'Sulit',         'MAHIR',    12),
(1, 'Arek-Arek',          'Sangat Sulit',  'MAHIR',    14),
(2, 'Lawang via Tretes',  'Sedang',        'MENENGAH',  8),
(3, 'via Tretes',         'Mudah-Sedang',  'PEMULA',    6);

-- -----------------------------------------------------------------------------
-- Titik Jalur — Semeru Ranu Pani (jalur_id=1)
-- -----------------------------------------------------------------------------
INSERT INTO titik_jalur (jalur_id, jenis, nama, latitude, longitude, deskripsi, urutan) VALUES
(1, 'BASECAMP',    'Basecamp Ranu Pani',     -8.013, 112.952, 'Titik awal pendakian via Ranu Pani, fasilitas MCK tersedia', 1),
(1, 'POS',         'Pos 1 Watu Rejeng',      -8.030, 112.940, 'Pos pertama, pemandangan tebing batu',                      2),
(1, 'POS',         'Pos 2 Kalimati',         -8.065, 112.930, 'Pos terakhir sebelum puncak, area camping luas',            3),
(1, 'SUMBER_AIR',  'Sumber Air Kalimati',    -8.066, 112.931, 'Sumber air terakhir sebelum summit push',                   4),
(1, 'TITIK_RAWAN', 'Tanjakan Cinta',         -8.050, 112.935, 'Tanjakan terjal, rawan kelelahan dan tergelincir',          5);

-- Titik Jalur — Semeru Arek-Arek (jalur_id=2)
INSERT INTO titik_jalur (jalur_id, jenis, nama, latitude, longitude, deskripsi, urutan) VALUES
(2, 'BASECAMP',    'Basecamp Arek-Arek',     -8.010, 112.945, 'Titik start jalur alternatif Semeru',           1),
(2, 'POS',         'Pos Oro-Oro Ombo',       -8.035, 112.938, 'Padang savana luas, spot foto populer',         2),
(2, 'SUMBER_AIR',  'Sumber Air Ranu Kumbolo',-8.050, 112.932, 'Danau Ranu Kumbolo, sumber air utama',          3),
(2, 'TITIK_RAWAN', 'Tanjakan Besi',          -8.070, 112.928, 'Tanjakan sangat curam menuju puncak Mahameru',  4);

-- Titik Jalur — Arjuno via Tretes (jalur_id=3)
INSERT INTO titik_jalur (jalur_id, jenis, nama, latitude, longitude, deskripsi, urutan) VALUES
(3, 'BASECAMP',    'Basecamp Tretes',         -7.720, 112.620, 'Basecamp utama pendakian Arjuno dari Tretes',           1),
(3, 'POS',         'Pos 1 Lalijiwo',          -7.740, 112.610, 'Pos peristirahatan, vegetasi hutan montane',            2),
(3, 'SUMBER_AIR',  'Sumber Air Lalijiwo',     -7.742, 112.608, 'Sumber air bersih di area Lalijiwo',                    3),
(3, 'TITIK_RAWAN', 'Angin Kencang Puncak',    -7.761, 112.596, 'Area puncak rawan angin kencang, waspadai cuaca buruk', 4);

-- Titik Jalur — Welirang via Tretes (jalur_id=4)
INSERT INTO titik_jalur (jalur_id, jenis, nama, latitude, longitude, deskripsi, urutan) VALUES
(4, 'BASECAMP',    'Basecamp Tretes',         -7.720, 112.620, 'Titik awal pendakian Welirang via Tretes',                    1),
(4, 'POS',         'Pos 1 Air Panas',         -7.735, 112.605, 'Pos dekat sumber air panas alami',                            2),
(4, 'SUMBER_AIR',  'Sumber Air Air Panas',    -7.736, 112.604, 'Sumber air panas, bisa untuk berendam',                       3),
(4, 'TITIK_RAWAN', 'Kawah Belerang',          -7.766, 112.577, 'Area kawah aktif, wajib pakai masker, hindari saat berangin', 4);

-- -----------------------------------------------------------------------------
-- Rencana Pendakian — sample plans
-- -----------------------------------------------------------------------------
INSERT INTO rencana_pendakian (pembuat_id, jalur_id, tanggal_naik, kuota, level_minimum, catatan, status) VALUES
(2, 1, '2026-11-15', 5, 'MAHIR',
 'Pendakian Semeru via Ranu Pani. Wajib bawa sleeping bag dan jaket tebal. Kumpul di Malang H-1.',
 'DIBUKA'),

(3, 4, '2026-11-22', 8, 'PEMULA',
 'Pendakian santai Welirang, cocok untuk pemula. Peralatan dasar disediakan.',
 'DIBUKA');

-- -----------------------------------------------------------------------------
-- Peserta Rencana — sample join requests
-- -----------------------------------------------------------------------------
INSERT INTO peserta_rencana (rencana_id, pengguna_id, status) VALUES
(1, 3, 'MENUNGGU'),   -- Sari mendaftar ke rencana Budi (Semeru)
(1, 4, 'DITOLAK'),    -- Andi ditolak (level PEMULA, rencana butuh MAHIR)
(2, 2, 'DITERIMA'),   -- Budi diterima di rencana Sari (Welirang)
(2, 4, 'MENUNGGU');    -- Andi mendaftar ke rencana Sari (Welirang)

-- -----------------------------------------------------------------------------
-- Panduan Keselamatan
-- -----------------------------------------------------------------------------
INSERT INTO panduan_keselamatan (judul, isi, kategori) VALUES
('Pencegahan Hipotermia',
 'Hipotermia terjadi saat suhu tubuh turun di bawah 35°C. Untuk mencegahnya:\n'
 '1. Gunakan sistem layering: base layer, mid layer, dan outer layer.\n'
 '2. Hindari pakaian basah — bawa pakaian ganti dalam dry bag.\n'
 '3. Konsumsi makanan berkalori tinggi dan minuman hangat secara teratur.\n'
 '4. Segera pasang bivak atau tenda jika menggigil tak terkendali.\n'
 '5. Jangan mendaki sendirian — sistem buddy membantu deteksi dini gejala.',
 'Kesehatan'),

('Mengenali dan Mengatasi Altitude Sickness',
 'Acute Mountain Sickness (AMS) bisa terjadi di ketinggian > 2.500 mdpl. Gejala:\n'
 '- Sakit kepala, mual, pusing, sulit tidur.\n'
 'Pencegahan:\n'
 '1. Aklimatisasi bertahap — jangan naikkan ketinggian tidur lebih dari 500m/hari.\n'
 '2. Minum air minimal 3 liter/hari.\n'
 '3. Hindari alkohol dan obat tidur.\n'
 'Penanganan: Jika gejala memberat, TURUN SEGERA minimal 500m. Jangan paksakan naik.',
 'Kesehatan'),

('Tips Navigasi di Gunung',
 'Tersesat adalah penyebab utama kecelakaan pendakian. Tips navigasi:\n'
 '1. Pelajari rute sebelum berangkat — unduh peta offline (misalnya via Avenza Maps).\n'
 '2. Bawa kompas dan peta fisik sebagai cadangan.\n'
 '3. Perhatikan tanda jalur (pita, cat di batu, cairn) dan catat waktu di setiap pos.\n'
 '4. Jika tersesat: STOP — Sit, Think, Observe, Plan. Jangan panik.\n'
 '5. Gunakan peluit untuk sinyal darurat (3x tiup berulang).',
 'Navigasi'),

('Protokol Darurat di Gunung',
 'Jika terjadi kecelakaan atau kondisi darurat:\n'
 '1. Pastikan keselamatan diri dan tim terlebih dahulu.\n'
 '2. Berikan pertolongan pertama (P3K) sesuai kemampuan.\n'
 '3. Hubungi pos SAR terdekat atau telepon 112 / Basarnas 115.\n'
 '4. Kirim minimal 2 orang untuk mencari bantuan jika tidak ada sinyal.\n'
 '5. Jangan pindahkan korban cedera tulang belakang.\n'
 '6. Nyalakan suar atau gunakan peluit dan cermin untuk menarik perhatian tim SAR.\n'
 '7. Selalu informasikan rencana pendakian ke keluarga dan pihak basecamp sebelum naik.',
 'Darurat');
