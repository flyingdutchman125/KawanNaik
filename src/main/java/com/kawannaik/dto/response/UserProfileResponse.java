package com.kawannaik.dto.response;

import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long id;
    private String nama;
    private String email;
    private Peran peran;
    private LevelFisik levelFisik;
    private String pengalaman;
    private String kontakDarurat;
    private LocalDateTime createdAt;

    public UserProfileResponse() {
        super();
    }

    public UserProfileResponse(Long id, String nama, String email, Peran peran, LevelFisik levelFisik, String pengalaman, String kontakDarurat, LocalDateTime createdAt) {
        super();
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.peran = peran;
        this.levelFisik = levelFisik;
        this.pengalaman = pengalaman;
        this.kontakDarurat = kontakDarurat;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nama;
        private String email;
        private Peran peran;
        private LevelFisik levelFisik;
        private String pengalaman;
        private String kontakDarurat;
        private LocalDateTime createdAt;

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

        public UserProfileResponse build() {
            return new UserProfileResponse(id, nama, email, peran, levelFisik, pengalaman, kontakDarurat, createdAt);
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
}
