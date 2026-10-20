package com.kawannaik.repository;

import com.kawannaik.model.Pengguna;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.Peran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Long> {
    Optional<Pengguna> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Pengguna> findByPeran(Peran peran);
    List<Pengguna> findByLevelFisik(LevelFisik levelFisik);
}
