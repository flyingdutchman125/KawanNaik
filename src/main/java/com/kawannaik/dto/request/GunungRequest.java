package com.kawannaik.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GunungRequest {

    @NotBlank(message = "Nama gunung tidak boleh kosong")
    @Size(max = 100, message = "Nama gunung maksimal 100 karakter")
    private String nama;

    @NotBlank(message = "Lokasi tidak boleh kosong")
    @Size(max = 255, message = "Lokasi maksimal 255 karakter")
    private String lokasi;

    @NotNull(message = "Ketinggian mdpl tidak boleh kosong")
    private Integer ketinggianMdpl;

    private Double latitude;

    private Double longitude;

    @Size(max = 50, message = "Kontak basecamp maksimal 50 karakter")
    private String kontakBasecamp;

    // Format Kepmendagri tingkat IV, contoh 35.08.05.2002
    @jakarta.validation.constraints.Pattern(regexp = "^$|^\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{4}$", message = "Kode wilayah harus format adm4, contoh 35.08.05.2002")
    private String kodeWilayah;

    public GunungRequest() {
        super();
    }

    public GunungRequest(String nama, String lokasi, Integer ketinggianMdpl, Double latitude, Double longitude, String kontakBasecamp, String kodeWilayah) {
        super();
        this.nama = nama;
        this.lokasi = lokasi;
        this.ketinggianMdpl = ketinggianMdpl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kontakBasecamp = kontakBasecamp;
        this.kodeWilayah = kodeWilayah;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nama;
        private String lokasi;
        private Integer ketinggianMdpl;
        private Double latitude;
        private Double longitude;
        private String kontakBasecamp;
        private String kodeWilayah;

        public Builder nama(String nama) {
            this.nama = nama;
            return this;
        }

        public Builder lokasi(String lokasi) {
            this.lokasi = lokasi;
            return this;
        }

        public Builder ketinggianMdpl(Integer ketinggianMdpl) {
            this.ketinggianMdpl = ketinggianMdpl;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder kontakBasecamp(String kontakBasecamp) {
            this.kontakBasecamp = kontakBasecamp;
            return this;
        }

        public Builder kodeWilayah(String kodeWilayah) {
            this.kodeWilayah = kodeWilayah;
            return this;
        }

        public GunungRequest build() {
            return new GunungRequest(nama, lokasi, ketinggianMdpl, latitude, longitude, kontakBasecamp, kodeWilayah);
        }
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public Integer getKetinggianMdpl() {
        return ketinggianMdpl;
    }

    public void setKetinggianMdpl(Integer ketinggianMdpl) {
        this.ketinggianMdpl = ketinggianMdpl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getKontakBasecamp() {
        return kontakBasecamp;
    }

    public void setKontakBasecamp(String kontakBasecamp) {
        this.kontakBasecamp = kontakBasecamp;
    }

    public String getKodeWilayah() {
        return kodeWilayah;
    }

    public void setKodeWilayah(String kodeWilayah) {
        this.kodeWilayah = kodeWilayah;
    }
}
