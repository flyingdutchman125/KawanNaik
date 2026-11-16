package com.kawannaik.dto.request;

import com.kawannaik.model.enums.LevelFisik;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RencanaPendakianRequest {

    @NotNull(message = "Jalur ID tidak boleh kosong")
    private Long jalurId;

    @NotNull(message = "Tanggal naik tidak boleh kosong")
    @FutureOrPresent(message = "Tanggal pendakian tidak boleh di masa lalu")
    private LocalDate tanggalNaik;

    private String waktuKumpul;

    private String titikKumpul;

    private String perlengkapan;

    private Integer estimasiBiaya;

    private String kontak;

    @NotNull(message = "Kuota tidak boleh kosong")
    @Min(value = 1, message = "Kuota minimal 1 orang")
    private Integer kuota;

    private LevelFisik levelMinimum;

    private String catatan;

    public RencanaPendakianRequest() {
        super();
    }

    public RencanaPendakianRequest(Long jalurId, LocalDate tanggalNaik, Integer kuota, LevelFisik levelMinimum, String catatan) {
        this(jalurId, tanggalNaik, null, null, null, null, null, kuota, levelMinimum, catatan);
    }

    public RencanaPendakianRequest(Long jalurId, LocalDate tanggalNaik, String waktuKumpul, String titikKumpul, String perlengkapan, Integer estimasiBiaya, String kontak, Integer kuota, LevelFisik levelMinimum, String catatan) {
        super();
        this.jalurId = jalurId;
        this.tanggalNaik = tanggalNaik;
        this.waktuKumpul = waktuKumpul;
        this.titikKumpul = titikKumpul;
        this.perlengkapan = perlengkapan;
        this.estimasiBiaya = estimasiBiaya;
        this.kontak = kontak;
        this.kuota = kuota;
        this.levelMinimum = levelMinimum;
        this.catatan = catatan;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long jalurId;
        private LocalDate tanggalNaik;
        private String waktuKumpul;
        private String titikKumpul;
        private String perlengkapan;
        private Integer estimasiBiaya;
        private String kontak;
        private Integer kuota;
        private LevelFisik levelMinimum;
        private String catatan;

        public Builder jalurId(Long jalurId) {
            this.jalurId = jalurId;
            return this;
        }

        public Builder tanggalNaik(LocalDate tanggalNaik) {
            this.tanggalNaik = tanggalNaik;
            return this;
        }

        public Builder waktuKumpul(String waktuKumpul) {
            this.waktuKumpul = waktuKumpul;
            return this;
        }

        public Builder titikKumpul(String titikKumpul) {
            this.titikKumpul = titikKumpul;
            return this;
        }

        public Builder perlengkapan(String perlengkapan) {
            this.perlengkapan = perlengkapan;
            return this;
        }

        public Builder estimasiBiaya(Integer estimasiBiaya) {
            this.estimasiBiaya = estimasiBiaya;
            return this;
        }

        public Builder kontak(String kontak) {
            this.kontak = kontak;
            return this;
        }

        public Builder kuota(Integer kuota) {
            this.kuota = kuota;
            return this;
        }

        public Builder levelMinimum(LevelFisik levelMinimum) {
            this.levelMinimum = levelMinimum;
            return this;
        }

        public Builder catatan(String catatan) {
            this.catatan = catatan;
            return this;
        }

        public RencanaPendakianRequest build() {
            return new RencanaPendakianRequest(jalurId, tanggalNaik, waktuKumpul, titikKumpul, perlengkapan, estimasiBiaya, kontak, kuota, levelMinimum, catatan);
        }
    }

    public Long getJalurId() {
        return jalurId;
    }

    public void setJalurId(Long jalurId) {
        this.jalurId = jalurId;
    }

    public LocalDate getTanggalNaik() {
        return tanggalNaik;
    }

    public void setTanggalNaik(LocalDate tanggalNaik) {
        this.tanggalNaik = tanggalNaik;
    }

    public String getWaktuKumpul() {
        return waktuKumpul;
    }

    public void setWaktuKumpul(String waktuKumpul) {
        this.waktuKumpul = waktuKumpul;
    }

    public String getTitikKumpul() {
        return titikKumpul;
    }

    public void setTitikKumpul(String titikKumpul) {
        this.titikKumpul = titikKumpul;
    }

    public String getPerlengkapan() {
        return perlengkapan;
    }

    public void setPerlengkapan(String perlengkapan) {
        this.perlengkapan = perlengkapan;
    }

    public Integer getEstimasiBiaya() {
        return estimasiBiaya;
    }

    public void setEstimasiBiaya(Integer estimasiBiaya) {
        this.estimasiBiaya = estimasiBiaya;
    }

    public String getKontak() {
        return kontak;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }

    public Integer getKuota() {
        return kuota;
    }

    public void setKuota(Integer kuota) {
        this.kuota = kuota;
    }

    public LevelFisik getLevelMinimum() {
        return levelMinimum;
    }

    public void setLevelMinimum(LevelFisik levelMinimum) {
        this.levelMinimum = levelMinimum;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}
