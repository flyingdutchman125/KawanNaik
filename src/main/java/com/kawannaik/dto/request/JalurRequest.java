package com.kawannaik.dto.request;

import com.kawannaik.model.enums.LevelFisik;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JalurRequest {

    @NotNull(message = "Gunung ID tidak boleh kosong")
    private Long gunungId;

    @NotBlank(message = "Nama jalur tidak boleh kosong")
    @Size(max = 100, message = "Nama jalur maksimal 100 karakter")
    private String namaJalur;

    private Double jarakKm;

    @Size(max = 50, message = "Tingkat kesulitan maksimal 50 karakter")
    private String tingkatKesulitan;

    private LevelFisik levelMinimum;

    private Double estimasiJam;

    public JalurRequest() {
        super();
    }

    public JalurRequest(Long gunungId, String namaJalur, Double jarakKm, String tingkatKesulitan, LevelFisik levelMinimum, Double estimasiJam) {
        super();
        this.gunungId = gunungId;
        this.namaJalur = namaJalur;
        this.jarakKm = jarakKm;
        this.tingkatKesulitan = tingkatKesulitan;
        this.levelMinimum = levelMinimum;
        this.estimasiJam = estimasiJam;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long gunungId;
        private String namaJalur;
        private Double jarakKm;
        private String tingkatKesulitan;
        private LevelFisik levelMinimum;
        private Double estimasiJam;

        public Builder gunungId(Long gunungId) {
            this.gunungId = gunungId;
            return this;
        }

        public Builder namaJalur(String namaJalur) {
            this.namaJalur = namaJalur;
            return this;
        }

        public Builder jarakKm(Double jarakKm) {
            this.jarakKm = jarakKm;
            return this;
        }

        public Builder tingkatKesulitan(String tingkatKesulitan) {
            this.tingkatKesulitan = tingkatKesulitan;
            return this;
        }

        public Builder levelMinimum(LevelFisik levelMinimum) {
            this.levelMinimum = levelMinimum;
            return this;
        }

        public Builder estimasiJam(Double estimasiJam) {
            this.estimasiJam = estimasiJam;
            return this;
        }

        public JalurRequest build() {
            return new JalurRequest(gunungId, namaJalur, jarakKm, tingkatKesulitan, levelMinimum, estimasiJam);
        }
    }

    public Long getGunungId() {
        return gunungId;
    }

    public void setGunungId(Long gunungId) {
        this.gunungId = gunungId;
    }

    public String getNamaJalur() {
        return namaJalur;
    }

    public void setNamaJalur(String namaJalur) {
        this.namaJalur = namaJalur;
    }

    public Double getJarakKm() {
        return jarakKm;
    }

    public void setJarakKm(Double jarakKm) {
        this.jarakKm = jarakKm;
    }

    public String getTingkatKesulitan() {
        return tingkatKesulitan;
    }

    public void setTingkatKesulitan(String tingkatKesulitan) {
        this.tingkatKesulitan = tingkatKesulitan;
    }

    public LevelFisik getLevelMinimum() {
        return levelMinimum;
    }

    public void setLevelMinimum(LevelFisik levelMinimum) {
        this.levelMinimum = levelMinimum;
    }

    public Double getEstimasiJam() {
        return estimasiJam;
    }

    public void setEstimasiJam(Double estimasiJam) {
        this.estimasiJam = estimasiJam;
    }
}
