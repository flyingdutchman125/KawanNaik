package com.kawannaik.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusKelayakan;
import com.kawannaik.model.enums.StatusRencana;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rencana_pendakian")
public class RencanaPendakian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pembuat_id", nullable = false)
    private Pengguna pembuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jalur_id", nullable = false)
    private Jalur jalur;

    @Column(name = "tanggal_naik", nullable = false)
    private LocalDate tanggalNaik;

    @Column(name = "waktu_kumpul", length = 50)
    private String waktuKumpul;

    @Column(name = "titik_kumpul")
    private String titikKumpul;

    @Column(columnDefinition = "TEXT")
    private String perlengkapan;

    @Column(name = "estimasi_biaya")
    private Integer estimasiBiaya;

    @Column(length = 100)
    private String kontak;

    @Column(nullable = false)
    private Integer kuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_minimum", length = 20)
    private LevelFisik levelMinimum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_kelayakan", length = 20)
    private StatusKelayakan statusKelayakan;

    @Column(columnDefinition = "TEXT")
    private String catatan;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusRencana status = StatusRencana.DIBUKA;

    @OneToMany(mappedBy = "rencana", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PesertaRencana> pesertaList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RencanaPendakian() {
        super();
    }

    public RencanaPendakian(Long id, Pengguna pembuat, Jalur jalur, LocalDate tanggalNaik, Integer kuota, LevelFisik levelMinimum, StatusKelayakan statusKelayakan, String catatan, StatusRencana status, List<PesertaRencana> pesertaList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, pembuat, jalur, tanggalNaik, null, null, null, null, null, kuota, levelMinimum, statusKelayakan, catatan, status, pesertaList, createdAt, updatedAt);
    }

    public RencanaPendakian(Long id, Pengguna pembuat, Jalur jalur, LocalDate tanggalNaik, String waktuKumpul, String titikKumpul, String perlengkapan, Integer estimasiBiaya, String kontak, Integer kuota, LevelFisik levelMinimum, StatusKelayakan statusKelayakan, String catatan, StatusRencana status, List<PesertaRencana> pesertaList, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.pembuat = pembuat;
        this.jalur = jalur;
        this.tanggalNaik = tanggalNaik;
        this.waktuKumpul = waktuKumpul;
        this.titikKumpul = titikKumpul;
        this.perlengkapan = perlengkapan;
        this.estimasiBiaya = estimasiBiaya;
        this.kontak = kontak;
        this.kuota = kuota;
        this.levelMinimum = levelMinimum;
        this.statusKelayakan = statusKelayakan;
        this.catatan = catatan;
        this.status = status != null ? status : StatusRencana.DIBUKA;
        this.pesertaList = pesertaList != null ? pesertaList : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Pengguna pembuat;
        private Jalur jalur;
        private LocalDate tanggalNaik;
        private String waktuKumpul;
        private String titikKumpul;
        private String perlengkapan;
        private Integer estimasiBiaya;
        private String kontak;
        private Integer kuota;
        private LevelFisik levelMinimum;
        private StatusKelayakan statusKelayakan;
        private String catatan;
        private StatusRencana status = StatusRencana.DIBUKA;
        private List<PesertaRencana> pesertaList = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pembuat(Pengguna pembuat) {
            this.pembuat = pembuat;
            return this;
        }

        public Builder jalur(Jalur jalur) {
            this.jalur = jalur;
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

        public Builder pesertaList(List<PesertaRencana> pesertaList) {
            this.pesertaList = pesertaList;
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

        public RencanaPendakian build() {
            return new RencanaPendakian(id, pembuat, jalur, tanggalNaik, waktuKumpul, titikKumpul, perlengkapan, estimasiBiaya, kontak, kuota, levelMinimum, statusKelayakan, catatan, status, pesertaList, createdAt, updatedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pengguna getPembuat() {
        return pembuat;
    }

    public void setPembuat(Pengguna pembuat) {
        this.pembuat = pembuat;
    }

    public Jalur getJalur() {
        return jalur;
    }

    public void setJalur(Jalur jalur) {
        this.jalur = jalur;
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

    public List<PesertaRencana> getPesertaList() {
        return pesertaList;
    }

    public void setPesertaList(List<PesertaRencana> pesertaList) {
        this.pesertaList = pesertaList;
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
