package com.kawannaik.repository;

import com.kawannaik.model.TitikJalur;
import com.kawannaik.model.enums.JenisTitik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitikJalurRepository extends JpaRepository<TitikJalur, Long> {
    List<TitikJalur> findByJalurIdOrderByUrutanAsc(Long jalurId);
    List<TitikJalur> findByJalurIdAndJenis(Long jalurId, JenisTitik jenis);
}
