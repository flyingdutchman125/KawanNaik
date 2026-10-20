package com.kawannaik.repository;

import com.kawannaik.model.RencanaPendakian;
import com.kawannaik.model.enums.LevelFisik;
import com.kawannaik.model.enums.StatusRencana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RencanaPendakianRepository extends JpaRepository<RencanaPendakian, Long> {
    List<RencanaPendakian> findByPembuatId(Long pembuatId);
    List<RencanaPendakian> findByStatus(StatusRencana status);
    List<RencanaPendakian> findByJalurGunungId(Long gunungId);

    @Query("SELECT r FROM RencanaPendakian r WHERE " +
           "(:gunungId IS NULL OR r.jalur.gunung.id = :gunungId) AND " +
           "(:level IS NULL OR r.levelMinimum = :level) AND " +
           "(:tanggal IS NULL OR r.tanggalNaik = :tanggal) AND " +
           "(r.status = 'DIBUKA')")
    List<RencanaPendakian> searchRencana(
            @Param("gunungId") Long gunungId,
            @Param("level") LevelFisik level,
            @Param("tanggal") LocalDate tanggal);
}
