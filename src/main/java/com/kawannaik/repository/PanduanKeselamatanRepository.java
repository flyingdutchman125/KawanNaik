package com.kawannaik.repository;

import com.kawannaik.model.PanduanKeselamatan;
import com.kawannaik.model.enums.LevelFisik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanduanKeselamatanRepository extends JpaRepository<PanduanKeselamatan, Long> {
    List<PanduanKeselamatan> findByKategoriIgnoreCase(String kategori);
    List<PanduanKeselamatan> findByLevelMinimum(LevelFisik levelMinimum);
}
