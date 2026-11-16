package com.kawannaik.dto.response;

import java.util.List;

/**
 * Prakiraan cuaca BMKG untuk satu gunung.
 * Sumber data: Badan Meteorologi, Klimatologi, dan Geofisika (BMKG).
 */
public class CuacaResponse {

    private Long gunungId;
    private String namaGunung;
    private String kodeWilayah;
    private String desa;
    private String kecamatan;
    private String kota;
    private String sumber = "BMKG (Badan Meteorologi, Klimatologi, dan Geofisika)";
    private List<Slot> prakiraan;

    public static class Slot {
        private String localDatetime;
        private Integer suhuC;
        private Integer kelembapanPersen;
        private String kondisi;
        private Double kecepatanAnginKmh;
        private String arahAngin;

        public Slot() {
        }

        public Slot(String localDatetime, Integer suhuC, Integer kelembapanPersen, String kondisi, Double kecepatanAnginKmh, String arahAngin) {
            this.localDatetime = localDatetime;
            this.suhuC = suhuC;
            this.kelembapanPersen = kelembapanPersen;
            this.kondisi = kondisi;
            this.kecepatanAnginKmh = kecepatanAnginKmh;
            this.arahAngin = arahAngin;
        }

        public String getLocalDatetime() {
            return localDatetime;
        }

        public Integer getSuhuC() {
            return suhuC;
        }

        public Integer getKelembapanPersen() {
            return kelembapanPersen;
        }

        public String getKondisi() {
            return kondisi;
        }

        public Double getKecepatanAnginKmh() {
            return kecepatanAnginKmh;
        }

        public String getArahAngin() {
            return arahAngin;
        }
    }

    public Long getGunungId() {
        return gunungId;
    }

    public void setGunungId(Long gunungId) {
        this.gunungId = gunungId;
    }

    public String getNamaGunung() {
        return namaGunung;
    }

    public void setNamaGunung(String namaGunung) {
        this.namaGunung = namaGunung;
    }

    public String getKodeWilayah() {
        return kodeWilayah;
    }

    public void setKodeWilayah(String kodeWilayah) {
        this.kodeWilayah = kodeWilayah;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getSumber() {
        return sumber;
    }

    public List<Slot> getPrakiraan() {
        return prakiraan;
    }

    public void setPrakiraan(List<Slot> prakiraan) {
        this.prakiraan = prakiraan;
    }
}
