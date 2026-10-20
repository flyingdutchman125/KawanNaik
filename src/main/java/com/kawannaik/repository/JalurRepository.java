package com.kawannaik.repository;

import com.kawannaik.model.Jalur;
import com.kawannaik.model.enums.LevelFisik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JalurRepository extends JpaRepository<Jalur, Long> {
    List<Jalur> findByGunungId(Long gunungId);
    List<Jalur> findByLevelMinimum(LevelFisik levelMinimum);
}
