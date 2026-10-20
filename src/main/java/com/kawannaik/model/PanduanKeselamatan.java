package com.kawannaik.model;

import com.kawannaik.model.enums.LevelFisik;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "panduan_keselamatan")
public class PanduanKeselamatan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String judul;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String isi;

    @Column(length = 100)
    private String kategori;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_minimum", length = 20)
    private LevelFisik levelMinimum;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PanduanKeselamatan() {
        super();
    }

    public PanduanKeselamatan(Long id, String judul, String isi, String kategori, LevelFisik levelMinimum, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.judul = judul;
        this.isi = isi;
        this.kategori = kategori;
        this.levelMinimum = levelMinimum;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String judul;
        private String isi;
        private String kategori;
        private LevelFisik levelMinimum;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder judul(String judul) {
            this.judul = judul;
            return this;
        }

        public Builder isi(String isi) {
            this.isi = isi;
            return this;
        }

        public Builder kategori(String kategori) {
            this.kategori = kategori;
            return this;
        }

        public Builder levelMinimum(LevelFisik levelMinimum) {
            this.levelMinimum = levelMinimum;
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

        public PanduanKeselamatan build() {
            return new PanduanKeselamatan(id, judul, isi, kategori, levelMinimum, createdAt, updatedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public LevelFisik getLevelMinimum() {
        return levelMinimum;
    }

    public void setLevelMinimum(LevelFisik levelMinimum) {
        this.levelMinimum = levelMinimum;
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
