package com.kawannaik.repository;

import com.kawannaik.model.Gunung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GunungRepository extends JpaRepository<Gunung, Long> {
    List<Gunung> findByNamaContainingIgnoreCase(String nama);
    List<Gunung> findByLokasiContainingIgnoreCase(String lokasi);
}
