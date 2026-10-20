package com.kawannaik.dto.response;

import com.kawannaik.model.enums.JenisTitik;

public class TitikJalurResponse {
    private Long id;
    private Long jalurId;
    private JenisTitik jenis;
    private String nama;
    private Double latitude;
    private Double longitude;
    private String deskripsi;
    private Integer urutan;

    public TitikJalurResponse() {
        super();
    }

    public TitikJalurResponse(Long id, Long jalurId, JenisTitik jenis, String nama, Double latitude, Double longitude, String deskripsi, Integer urutan) {
        super();
        this.id = id;
        this.jalurId = jalurId;
        this.jenis = jenis;
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deskripsi = deskripsi;
        this.urutan = urutan;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long jalurId;
        private JenisTitik jenis;
        private String nama;
        private Double latitude;
        private Double longitude;
        private String deskripsi;
        private Integer urutan;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder jalurId(Long jalurId) {
            this.jalurId = jalurId;
            return this;
        }

        public Builder jenis(JenisTitik jenis) {
            this.jenis = jenis;
            return this;
        }

        public Builder nama(String nama) {
            this.nama = nama;
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

        public Builder deskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
            return this;
        }

        public Builder urutan(Integer urutan) {
            this.urutan = urutan;
            return this;
        }

        public TitikJalurResponse build() {
            return new TitikJalurResponse(id, jalurId, jenis, nama, latitude, longitude, deskripsi, urutan);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJalurId() {
        return jalurId;
    }

    public void setJalurId(Long jalurId) {
        this.jalurId = jalurId;
    }

    public JenisTitik getJenis() {
        return jenis;
    }

    public void setJenis(JenisTitik jenis) {
        this.jenis = jenis;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Integer getUrutan() {
        return urutan;
    }

    public void setUrutan(Integer urutan) {
        this.urutan = urutan;
    }
}
