package com.kawannaik.dto.response;

import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusKelayakan;
import com.kawannaik.model.enums.StatusRencana;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RencanaPendakianResponse {
    private Long id;
    private Long pembuatId;
    private String namaPembuat;
    private LevelFisik levelPembuat;
    private Long jalurId;
    private String namaJalur;
    private String namaGunung;
    private LocalDate tanggalNaik;
    private Integer kuota;
    private Long jumlahPesertaDiterima;
    private LevelFisik levelMinimum;
    private StatusKelayakan statusKelayakan;
    private String catatan;
    private StatusRencana status;
    private LocalDateTime createdAt;

    public RencanaPendakianResponse() {
        super();
    }

    public RencanaPendakianResponse(Long id, Long pembuatId, String namaPembuat, LevelFisik levelPembuat, Long jalurId, String namaJalur, String namaGunung, LocalDate tanggalNaik, Integer kuota, Long jumlahPesertaDiterima, LevelFisik levelMinimum, StatusKelayakan statusKelayakan, String catatan, StatusRencana status, LocalDateTime createdAt) {
        super();
        this.id = id;
        this.pembuatId = pembuatId;
        this.namaPembuat = namaPembuat;
        this.levelPembuat = levelPembuat;
        this.jalurId = jalurId;
        this.namaJalur = namaJalur;
        this.namaGunung = namaGunung;
        this.tanggalNaik = tanggalNaik;
        this.kuota = kuota;
        this.jumlahPesertaDiterima = jumlahPesertaDiterima;
        this.levelMinimum = levelMinimum;
        this.statusKelayakan = statusKelayakan;
        this.catatan = catatan;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long pembuatId;
        private String namaPembuat;
        private LevelFisik levelPembuat;
        private Long jalurId;
        private String namaJalur;
        private String namaGunung;
        private LocalDate tanggalNaik;
        private Integer kuota;
        private Long jumlahPesertaDiterima;
        private LevelFisik levelMinimum;
        private StatusKelayakan statusKelayakan;
        private String catatan;
        private StatusRencana status;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pembuatId(Long pembuatId) {
            this.pembuatId = pembuatId;
            return this;
        }

        public Builder namaPembuat(String namaPembuat) {
            this.namaPembuat = namaPembuat;
            return this;
        }

        public Builder levelPembuat(LevelFisik levelPembuat) {
            this.levelPembuat = levelPembuat;
            return this;
        }

        public Builder jalurId(Long jalurId) {
            this.jalurId = jalurId;
            return this;
        }

        public Builder namaJalur(String namaJalur) {
            this.namaJalur = namaJalur;
            return this;
        }

        public Builder namaGunung(String namaGunung) {
            this.namaGunung = namaGunung;
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

        public Builder jumlahPesertaDiterima(Long jumlahPesertaDiterima) {
            this.jumlahPesertaDiterima = jumlahPesertaDiterima;
            return this;
        }

        public Builder levelMinimum(LevelFisik levelMinimum) {
            this.levelMinimum = levelMinimum;
            return this;
        }

        public Builder statusKelayakan(StatusKelayakan statusKelayakan) {
            this.statusKelayakan = statusKelayakan;
            return this;
        }

        public Builder catatan(String catatan) {
            this.catatan = catatan;
            return this;
        }

        public Builder status(StatusRencana status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RencanaPendakianResponse build() {
            return new RencanaPendakianResponse(id, pembuatId, namaPembuat, levelPembuat, jalurId, namaJalur, namaGunung, tanggalNaik, kuota, jumlahPesertaDiterima, levelMinimum, statusKelayakan, catatan, status, createdAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPembuatId() {
        return pembuatId;
    }

    public void setPembuatId(Long pembuatId) {
        this.pembuatId = pembuatId;
    }

    public String getNamaPembuat() {
        return namaPembuat;
    }

    public void setNamaPembuat(String namaPembuat) {
        this.namaPembuat = namaPembuat;
    }

    public LevelFisik getLevelPembuat() {
        return levelPembuat;
    }

    public void setLevelPembuat(LevelFisik levelPembuat) {
        this.levelPembuat = levelPembuat;
    }

    public Long getJalurId() {
        return jalurId;
    }

    public void setJalurId(Long jalurId) {
        this.jalurId = jalurId;
    }

    public String getNamaJalur() {
        return namaJalur;
    }

    public void setNamaJalur(String namaJalur) {
        this.namaJalur = namaJalur;
    }

    public String getNamaGunung() {
        return namaGunung;
    }

    public void setNamaGunung(String namaGunung) {
        this.namaGunung = namaGunung;
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

    public Long getJumlahPesertaDiterima() {
        return jumlahPesertaDiterima;
    }

    public void setJumlahPesertaDiterima(Long jumlahPesertaDiterima) {
        this.jumlahPesertaDiterima = jumlahPesertaDiterima;
    }

    public LevelFisik getLevelMinimum() {
        return levelMinimum;
    }

    public void setLevelMinimum(LevelFisik levelMinimum) {
        this.levelMinimum = levelMinimum;
    }

    public StatusKelayakan getStatusKelayakan() {
        return statusKelayakan;
    }

    public void setStatusKelayakan(StatusKelayakan statusKelayakan) {
        this.statusKelayakan = statusKelayakan;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public StatusRencana getStatus() {
        return status;
    }

    public void setStatus(StatusRencana status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
