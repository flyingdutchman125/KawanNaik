package com.kawannaik.dto.request;

import com.kawannaik.model.enums.LevelFisik;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PanduanKeselamatanRequest {

    @NotBlank(message = "Judul tidak boleh kosong")
    @Size(max = 200, message = "Judul maksimal 200 karakter")
    private String judul;

    @NotBlank(message = "Isi panduan tidak boleh kosong")
    private String isi;

    @Size(max = 100, message = "Kategori maksimal 100 karakter")
    private String kategori;

    private LevelFisik levelMinimum;

    public PanduanKeselamatanRequest() {
        super();
    }

    public PanduanKeselamatanRequest(String judul, String isi, String kategori, LevelFisik levelMinimum) {
        super();
        this.judul = judul;
        this.isi = isi;
        this.kategori = kategori;
        this.levelMinimum = levelMinimum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String judul;
        private String isi;
        private String kategori;
        private LevelFisik levelMinimum;

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

        public PanduanKeselamatanRequest build() {
            return new PanduanKeselamatanRequest(judul, isi, kategori, levelMinimum);
        }
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
}
