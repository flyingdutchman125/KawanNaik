package com.kawannaik.dto.response;

import com.kawannaik.model.enums.LevelFisik;

import java.util.List;

public class JalurResponse {
    private Long id;
    private Long gunungId;
    private String namaGunung;
    private String namaJalur;
    private Double jarakKm;
    private String tingkatKesulitan;
    private LevelFisik levelMinimum;
    private Double estimasiJam;
    private List<TitikJalurResponse> titikJalurList;

    public JalurResponse() {
        super();
    }

    public JalurResponse(Long id, Long gunungId, String namaGunung, String namaJalur, Double jarakKm, String tingkatKesulitan, LevelFisik levelMinimum, Double estimasiJam, List<TitikJalurResponse> titikJalurList) {
        super();
        this.id = id;
        this.gunungId = gunungId;
        this.namaGunung = namaGunung;
        this.namaJalur = namaJalur;
        this.jarakKm = jarakKm;
        this.tingkatKesulitan = tingkatKesulitan;
        this.levelMinimum = levelMinimum;
        this.estimasiJam = estimasiJam;
        this.titikJalurList = titikJalurList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long gunungId;
        private String namaGunung;
        private String namaJalur;
        private Double jarakKm;
        private String tingkatKesulitan;
        private LevelFisik levelMinimum;
        private Double estimasiJam;
        private List<TitikJalurResponse> titikJalurList;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder gunungId(Long gunungId) {
            this.gunungId = gunungId;
            return this;
        }

        public Builder namaGunung(String namaGunung) {
            this.namaGunung = namaGunung;
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

        public Builder titikJalurList(List<TitikJalurResponse> titikJalurList) {
            this.titikJalurList = titikJalurList;
            return this;
        }

        public JalurResponse build() {
            return new JalurResponse(id, gunungId, namaGunung, namaJalur, jarakKm, tingkatKesulitan, levelMinimum, estimasiJam, titikJalurList);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGunungId() {
        return gunungId;
    }

    public void setGunungId(Long gunungId) {
        this.gunungId = gunungId;
    }

    public String getNamaGunung() {
        return namaGunung;
    }

    public void setNamaGunung(String namaGunung) {
        this.namaGunung = namaGunung;
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

    public List<TitikJalurResponse> getTitikJalurList() {
        return titikJalurList;
    }

    public void setTitikJalurList(List<TitikJalurResponse> titikJalurList) {
        this.titikJalurList = titikJalurList;
    }
}
