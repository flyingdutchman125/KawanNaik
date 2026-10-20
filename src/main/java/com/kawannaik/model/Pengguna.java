package com.kawannaik.model;

import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pengguna")
public class Pengguna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Peran peran = Peran.PENDAKI;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_fisik", nullable = false, length = 20)
    private LevelFisik levelFisik = LevelFisik.PEMULA;

    @Column(columnDefinition = "TEXT")
    private String pengalaman;

    @Column(name = "kontak_darurat")
    private String kontakDarurat;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Pengguna() {
        super();
    }

    public Pengguna(Long id, String nama, String email, String passwordHash, Peran peran, LevelFisik levelFisik, String pengalaman, String kontakDarurat, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super();
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.passwordHash = passwordHash;
        this.peran = peran != null ? peran : Peran.PENDAKI;
        this.levelFisik = levelFisik != null ? levelFisik : LevelFisik.PEMULA;
        this.pengalaman = pengalaman;
        this.kontakDarurat = kontakDarurat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nama;
        private String email;
        private String passwordHash;
        private Peran peran = Peran.PENDAKI;
        private LevelFisik levelFisik = LevelFisik.PEMULA;
        private String pengalaman;
        private String kontakDarurat;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nama(String nama) {
            this.nama = nama;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder peran(Peran peran) {
            this.peran = peran;
            return this;
        }

        public Builder levelFisik(LevelFisik levelFisik) {
            this.levelFisik = levelFisik;
            return this;
        }

        public Builder pengalaman(String pengalaman) {
            this.pengalaman = pengalaman;
            return this;
        }

        public Builder kontakDarurat(String kontakDarurat) {
            this.kontakDarurat = kontakDarurat;
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

        public Pengguna build() {
            return new Pengguna(id, nama, email, passwordHash, peran, levelFisik, pengalaman, kontakDarurat, createdAt, updatedAt);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Peran getPeran() {
        return peran;
    }

    public void setPeran(Peran peran) {
        this.peran = peran;
    }

    public LevelFisik getLevelFisik() {
        return levelFisik;
    }

    public void setLevelFisik(LevelFisik levelFisik) {
        this.levelFisik = levelFisik;
    }

    public String getPengalaman() {
        return pengalaman;
    }

    public void setPengalaman(String pengalaman) {
        this.pengalaman = pengalaman;
    }

    public String getKontakDarurat() {
        return kontakDarurat;
    }

    public void setKontakDarurat(String kontakDarurat) {
        this.kontakDarurat = kontakDarurat;
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
