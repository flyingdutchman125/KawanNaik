package com.kawannaik.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gunung")
public class Gunung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(nullable = false, length = 255)
    private String lokasi;

    @Column(name = "ketinggian_mdpl", nullable = false)
    private Integer ketinggianMdpl;

    private Double latitude;

    private Double longitude;

    @Column(name = "kontak_basecamp", length = 50)
    private String kontakBasecamp;

    @OneToMany(mappedBy = "gunung", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Jalur> jalurList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Gunung() {
        super();
    }

    public Gunung(Long id, String nama, String lokasi, Integer ketinggianMdpl, Double latitude, Double longitude, String kontakBasecamp, List<Jalur> jalurList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.nama = nama;
        this.lokasi = lokasi;
        this.ketinggianMdpl = ketinggianMdpl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kontakBasecamp = kontakBasecamp;
        this.jalurList = jalurList != null ? jalurList : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
        private List<Jalur> jalurList = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

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

        public Builder jalurList(List<Jalur> jalurList) {
            this.jalurList = jalurList;
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

        public Gunung build() {
            return new Gunung(id, nama, lokasi, ketinggianMdpl, latitude, longitude, kontakBasecamp, jalurList, createdAt, updatedAt);
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

    public List<Jalur> getJalurList() {
        return jalurList;
    }

    public void setJalurList(List<Jalur> jalurList) {
        this.jalurList = jalurList;
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
