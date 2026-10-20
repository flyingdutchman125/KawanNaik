package com.kawannaik.model;

import com.kawannaik.model.enums.StatusPermintaan;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "peserta_rencana", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rencana_id", "pengguna_id"})
})
public class PesertaRencana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rencana_id", nullable = false)
    private RencanaPendakian rencana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pengguna_id", nullable = false)
    private Pengguna pengguna;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusPermintaan status = StatusPermintaan.MENUNGGU;

    @CreationTimestamp
    @Column(name = "tanggal_ajuan", updatable = false)
    private LocalDateTime tanggalAjuan;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PesertaRencana() {
        super();
    }

    public PesertaRencana(Long id, RencanaPendakian rencana, Pengguna pengguna, StatusPermintaan status, LocalDateTime tanggalAjuan, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.rencana = rencana;
        this.pengguna = pengguna;
        this.status = status != null ? status : StatusPermintaan.MENUNGGU;
        this.tanggalAjuan = tanggalAjuan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private RencanaPendakian rencana;
        private Pengguna pengguna;
        private StatusPermintaan status = StatusPermintaan.MENUNGGU;
        private LocalDateTime tanggalAjuan;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder rencana(RencanaPendakian rencana) {
            this.rencana = rencana;
            return this;
        }

        public Builder pengguna(Pengguna pengguna) {
            this.pengguna = pengguna;
            return this;
        }

        public Builder status(StatusPermintaan status) {
            this.status = status;
            return this;
        }

        public Builder tanggalAjuan(LocalDateTime tanggalAjuan) {
            this.tanggalAjuan = tanggalAjuan;
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

        public PesertaRencana build() {
            return new PesertaRencana(id, rencana, pengguna, status, tanggalAjuan, createdAt, updatedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RencanaPendakian getRencana() {
        return rencana;
    }

    public void setRencana(RencanaPendakian rencana) {
        this.rencana = rencana;
    }

    public Pengguna getPengguna() {
        return pengguna;
    }

    public void setPengguna(Pengguna pengguna) {
        this.pengguna = pengguna;
    }

    public StatusPermintaan getStatus() {
        return status;
    }

    public void setStatus(StatusPermintaan status) {
        this.status = status;
    }

    public LocalDateTime getTanggalAjuan() {
        return tanggalAjuan;
    }

    public void setTanggalAjuan(LocalDateTime tanggalAjuan) {
        this.tanggalAjuan = tanggalAjuan;
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
