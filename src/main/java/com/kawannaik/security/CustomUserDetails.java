package com.kawannaik.security;

import com.kawannaik.model.Pengguna;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Pengguna pengguna;

    public CustomUserDetails(Pengguna pengguna) {
        this.pengguna = pengguna;
    }

    public Long getId() {
        return pengguna.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + pengguna.getPeran().name()));
    }

    @Override
    public String getPassword() {
        return pengguna.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return pengguna.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
