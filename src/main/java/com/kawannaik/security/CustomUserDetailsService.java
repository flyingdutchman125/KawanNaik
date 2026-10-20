package com.kawannaik.security;

import com.kawannaik.model.Pengguna;
import com.kawannaik.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PenggunaRepository penggunaRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Pengguna pengguna = penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Pengguna tidak ditemukan dengan email: " + email));
        return new CustomUserDetails(pengguna);
    }
}
