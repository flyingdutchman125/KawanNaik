package com.kawannaik.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kawannaik.model.enums.LevelFisik;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jalur")
public class Jalur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gunung_id", nullable = false)
    private Gunung gunung;

    @Column(name = "nama_jalur", nullable = false, length = 100)
    private String namaJalur;

    @Column(name = "jarak_km")
    private Double jarakKm;

    @Column(name = "tingkat_kesulitan", length = 50)
    private String tingkatKesulitan;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_minimum", nullable = false, length = 20)
    private LevelFisik levelMinimum = LevelFisik.PEMULA;

    @Column(name = "estimasi_jam")
    private Double estimasiJam;

    @OneToMany(mappedBy = "jalur", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("urutan ASC")
    private List<TitikJalur> titikJalurList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Jalur() {
        super();
    }

    public Jalur(Long id, Gunung gunung, String namaJalur, Double jarakKm, String tingkatKesulitan, LevelFisik levelMinimum, Double estimasiJam, List<TitikJalur> titikJalurList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.gunung = gunung;
        this.namaJalur = namaJalur;
        this.jarakKm = jarakKm;
        this.tingkatKesulitan = tingkatKesulitan;
        this.levelMinimum = levelMinimum != null ? levelMinimum : LevelFisik.PEMULA;
        this.estimasiJam = estimasiJam;
        this.titikJalurList = titikJalurList != null ? titikJalurList : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Gunung gunung;
        private String namaJalur;
        private Double jarakKm;
        private String tingkatKesulitan;
        private LevelFisik levelMinimum = LevelFisik.PEMULA;
        private Double estimasiJam;
        private List<TitikJalur> titikJalurList = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder gunung(Gunung gunung) {
            this.gunung = gunung;
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

        public Builder titikJalurList(List<TitikJalur> titikJalurList) {
            this.titikJalurList = titikJalurList;
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

        public Jalur build() {
            return new Jalur(id, gunung, namaJalur, jarakKm, tingkatKesulitan, levelMinimum, estimasiJam, titikJalurList, createdAt, updatedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gunung getGunung() {
        return gunung;
    }

    public void setGunung(Gunung gunung) {
        this.gunung = gunung;
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

    public List<TitikJalur> getTitikJalurList() {
        return titikJalurList;
    }

    public void setTitikJalurList(List<TitikJalur> titikJalurList) {
        this.titikJalurList = titikJalurList;
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
