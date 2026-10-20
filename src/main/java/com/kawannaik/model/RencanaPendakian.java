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
        super();
        this.id = id;
        this.pembuat = pembuat;
        this.jalur = jalur;
        this.tanggalNaik = tanggalNaik;
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
            return new RencanaPendakian(id, pembuat, jalur, tanggalNaik, kuota, levelMinimum, statusKelayakan, catatan, status, pesertaList, createdAt, updatedAt);
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
