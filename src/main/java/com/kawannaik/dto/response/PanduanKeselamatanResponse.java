package com.kawannaik.dto.response;

import com.kawannaik.model.enums.LevelFisik;

import java.time.LocalDateTime;

public class PanduanKeselamatanResponse {
    private Long id;
    private String judul;
    private String isi;
    private String kategori;
    private LevelFisik levelMinimum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PanduanKeselamatanResponse() {
        super();
    }

    public PanduanKeselamatanResponse(Long id, String judul, String isi, String kategori, LevelFisik levelMinimum, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

        public PanduanKeselamatanResponse build() {
            return new PanduanKeselamatanResponse(id, judul, isi, kategori, levelMinimum, createdAt, updatedAt);
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
