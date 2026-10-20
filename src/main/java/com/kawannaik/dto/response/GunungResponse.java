package com.kawannaik.dto.response;

import java.time.LocalDateTime;

public class GunungResponse {
    private Long id;
    private String nama;
    private String lokasi;
    private Integer ketinggianMdpl;
    private Double latitude;
    private Double longitude;
    private String kontakBasecamp;
    private Integer jumlahJalur;
    private LocalDateTime createdAt;

    public GunungResponse() {
        super();
    }

    public GunungResponse(Long id, String nama, String lokasi, Integer ketinggianMdpl, Double latitude, Double longitude, String kontakBasecamp, Integer jumlahJalur, LocalDateTime createdAt) {
        super();
        this.id = id;
        this.nama = nama;
        this.lokasi = lokasi;
        this.ketinggianMdpl = ketinggianMdpl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kontakBasecamp = kontakBasecamp;
        this.jumlahJalur = jumlahJalur;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nama;
        private String lokasi;
        private Integer ketinggianMdpl;
        private Double latitude;
        private Double longitude;
        private String kontakBasecamp;
        private Integer jumlahJalur;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder jumlahJalur(Integer jumlahJalur) {
            this.jumlahJalur = jumlahJalur;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GunungResponse build() {
            return new GunungResponse(id, nama, lokasi, ketinggianMdpl, latitude, longitude, kontakBasecamp, jumlahJalur, createdAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getJumlahJalur() {
        return jumlahJalur;
    }

    public void setJumlahJalur(Integer jumlahJalur) {
        this.jumlahJalur = jumlahJalur;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
