package com.kawannaik.repository;

import com.kawannaik.model.PesertaRencana;
import com.kawannaik.model.enums.StatusPermintaan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PesertaRencanaRepository extends JpaRepository<PesertaRencana, Long> {
    List<PesertaRencana> findByRencanaId(Long rencanaId);
    List<PesertaRencana> findByPenggunaId(Long penggunaId);
    Optional<PesertaRencana> findByRencanaIdAndPenggunaId(Long rencanaId, Long penggunaId);
    boolean existsByRencanaIdAndPenggunaId(Long rencanaId, Long penggunaId);
    long countByRencanaIdAndStatus(Long rencanaId, StatusPermintaan status);
}
