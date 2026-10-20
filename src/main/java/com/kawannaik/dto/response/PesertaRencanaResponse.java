package com.kawannaik.dto.response;

import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusKelayakan;
import com.kawannaik.model.enums.StatusPermintaan;

import java.time.LocalDateTime;

public class PesertaRencanaResponse {
    private Long id;
    private Long rencanaId;
    private Long penggunaId;
    private String namaPengguna;
    private String emailPengguna;
    private LevelFisik levelFisikPengguna;
    private StatusPermintaan status;
    private StatusKelayakan statusKelayakan;
    private LocalDateTime tanggalAjuan;

    public PesertaRencanaResponse() {
        super();
    }

    public PesertaRencanaResponse(Long id, Long rencanaId, Long penggunaId, String namaPengguna, String emailPengguna, LevelFisik levelFisikPengguna, StatusPermintaan status, StatusKelayakan statusKelayakan, LocalDateTime tanggalAjuan) {
        super();
        this.id = id;
        this.rencanaId = rencanaId;
        this.penggunaId = penggunaId;
        this.namaPengguna = namaPengguna;
        this.emailPengguna = emailPengguna;
        this.levelFisikPengguna = levelFisikPengguna;
        this.status = status;
        this.statusKelayakan = statusKelayakan;
        this.tanggalAjuan = tanggalAjuan;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long rencanaId;
        private Long penggunaId;
        private String namaPengguna;
        private String emailPengguna;
        private LevelFisik levelFisikPengguna;
        private StatusPermintaan status;
        private StatusKelayakan statusKelayakan;
        private LocalDateTime tanggalAjuan;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder rencanaId(Long rencanaId) {
            this.rencanaId = rencanaId;
            return this;
        }

        public Builder penggunaId(Long penggunaId) {
            this.penggunaId = penggunaId;
            return this;
        }

        public Builder namaPengguna(String namaPengguna) {
            this.namaPengguna = namaPengguna;
            return this;
        }

        public Builder emailPengguna(String emailPengguna) {
            this.emailPengguna = emailPengguna;
            return this;
        }

        public Builder levelFisikPengguna(LevelFisik levelFisikPengguna) {
            this.levelFisikPengguna = levelFisikPengguna;
            return this;
        }

        public Builder status(StatusPermintaan status) {
            this.status = status;
            return this;
        }

        public Builder statusKelayakan(StatusKelayakan statusKelayakan) {
            this.statusKelayakan = statusKelayakan;
            return this;
        }

        public Builder tanggalAjuan(LocalDateTime tanggalAjuan) {
            this.tanggalAjuan = tanggalAjuan;
            return this;
        }

        public PesertaRencanaResponse build() {
            return new PesertaRencanaResponse(id, rencanaId, penggunaId, namaPengguna, emailPengguna, levelFisikPengguna, status, statusKelayakan, tanggalAjuan);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRencanaId() {
        return rencanaId;
    }

    public void setRencanaId(Long rencanaId) {
        this.rencanaId = rencanaId;
    }

    public Long getPenggunaId() {
        return penggunaId;
    }

    public void setPenggunaId(Long penggunaId) {
        this.penggunaId = penggunaId;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public void setNamaPengguna(String namaPengguna) {
        this.namaPengguna = namaPengguna;
    }

    public String getEmailPengguna() {
        return emailPengguna;
    }

    public void setEmailPengguna(String emailPengguna) {
        this.emailPengguna = emailPengguna;
    }

    public LevelFisik getLevelFisikPengguna() {
        return levelFisikPengguna;
    }

    public void setLevelFisikPengguna(LevelFisik levelFisikPengguna) {
        this.levelFisikPengguna = levelFisikPengguna;
    }

    public StatusPermintaan getStatus() {
        return status;
    }

    public void setStatus(StatusPermintaan status) {
        this.status = status;
    }

    public StatusKelayakan getStatusKelayakan() {
        return statusKelayakan;
    }

    public void setStatusKelayakan(StatusKelayakan statusKelayakan) {
        this.statusKelayakan = statusKelayakan;
    }

    public LocalDateTime getTanggalAjuan() {
        return tanggalAjuan;
    }

    public void setTanggalAjuan(LocalDateTime tanggalAjuan) {
        this.tanggalAjuan = tanggalAjuan;
    }
}
