package com.kawannaik.dto.request;

import com.kawannaik.model.enums.LevelFisik;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String nama;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email maksimal 100 karakter")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    private LevelFisik levelFisik;

    private String pengalaman;

    private String kontakDarurat;

    public RegisterRequest() {
    }

    public RegisterRequest(String nama, String email, String password, LevelFisik levelFisik, String pengalaman, String kontakDarurat) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.levelFisik = levelFisik;
        this.pengalaman = pengalaman;
        this.kontakDarurat = kontakDarurat;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nama;
        private String email;
        private String password;
        private LevelFisik levelFisik;
        private String pengalaman;
        private String kontakDarurat;

        public Builder nama(String nama) {
            this.nama = nama;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
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

        public RegisterRequest build() {
            return new RegisterRequest(nama, email, password, levelFisik, pengalaman, kontakDarurat);
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
