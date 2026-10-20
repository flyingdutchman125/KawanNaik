package com.kawannaik.dto.response;

import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String nama;
    private String email;
    private Peran peran;
    private LevelFisik levelFisik;

    public AuthResponse() {
        super();
    }

    public AuthResponse(String token, String tokenType, Long id, String nama, String email, Peran peran, LevelFisik levelFisik) {
        super();
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.peran = peran;
        this.levelFisik = levelFisik;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String tokenType = "Bearer";
        private Long id;
        private String nama;
        private String email;
        private Peran peran;
        private LevelFisik levelFisik;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

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

        public AuthResponse build() {
            return new AuthResponse(token, tokenType, id, nama, email, peran, levelFisik);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
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
}
