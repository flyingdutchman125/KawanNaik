package com.kawannaik.dto.request;

import com.kawannaik.model.enums.LevelFisik;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String nama;

    private LevelFisik levelFisik;

    private String pengalaman;

    private String kontakDarurat;

    @Size(min = 6, message = "Password baru minimal 6 karakter")
    private String passwordBaru;

    public UpdateProfileRequest() {
        super();
    }

    public UpdateProfileRequest(String nama, LevelFisik levelFisik, String pengalaman, String kontakDarurat, String passwordBaru) {
        super();
        this.nama = nama;
        this.levelFisik = levelFisik;
        this.pengalaman = pengalaman;
        this.kontakDarurat = kontakDarurat;
        this.passwordBaru = passwordBaru;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nama;
        private LevelFisik levelFisik;
        private String pengalaman;
        private String kontakDarurat;
        private String passwordBaru;

        public Builder nama(String nama) {
            this.nama = nama;
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

        public Builder passwordBaru(String passwordBaru) {
            this.passwordBaru = passwordBaru;
            return this;
        }

        public UpdateProfileRequest build() {
            return new UpdateProfileRequest(nama, levelFisik, pengalaman, kontakDarurat, passwordBaru);
        }
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getPasswordBaru() {
        return passwordBaru;
    }

    public void setPasswordBaru(String passwordBaru) {
        this.passwordBaru = passwordBaru;
    }
}
