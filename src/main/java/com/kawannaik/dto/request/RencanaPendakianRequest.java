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

    @NotNull(message = "Kuota tidak boleh kosong")
    @Min(value = 1, message = "Kuota minimal 1 orang")
    private Integer kuota;

    private LevelFisik levelMinimum;

    private String catatan;

    public RencanaPendakianRequest() {
        super();
    }

    public RencanaPendakianRequest(Long jalurId, LocalDate tanggalNaik, Integer kuota, LevelFisik levelMinimum, String catatan) {
        super();
        this.jalurId = jalurId;
        this.tanggalNaik = tanggalNaik;
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
            return new RencanaPendakianRequest(jalurId, tanggalNaik, kuota, levelMinimum, catatan);
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
