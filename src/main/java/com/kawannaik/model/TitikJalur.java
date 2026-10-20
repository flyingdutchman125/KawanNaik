package com.kawannaik.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kawannaik.model.enums.JenisTitik;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "titik_jalur")
public class TitikJalur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jalur_id", nullable = false)
    @JsonIgnore
    private Jalur jalur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JenisTitik jenis;

    @Column(nullable = false, length = 100)
    private String nama;

    private Double latitude;

    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Column(nullable = false)
    private Integer urutan;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TitikJalur() {
        super();
    }

    public TitikJalur(Long id, Jalur jalur, JenisTitik jenis, String nama, Double latitude, Double longitude, String deskripsi, Integer urutan, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.jalur = jalur;
        this.jenis = jenis;
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deskripsi = deskripsi;
        this.urutan = urutan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Jalur jalur;
        private JenisTitik jenis;
        private String nama;
        private Double latitude;
        private Double longitude;
        private String deskripsi;
        private Integer urutan;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder jalur(Jalur jalur) {
            this.jalur = jalur;
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TitikJalur build() {
            return new TitikJalur(id, jalur, jenis, nama, latitude, longitude, deskripsi, urutan, createdAt, updatedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jalur getJalur() {
        return jalur;
    }

    public void setJalur(Jalur jalur) {
        this.jalur = jalur;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
