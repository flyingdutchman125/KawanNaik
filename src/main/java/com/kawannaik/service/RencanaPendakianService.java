package com.kawannaik.service;

import com.kawannaik.dto.request.RencanaPendakianRequest;
import com.kawannaik.dto.response.RencanaPendakianResponse;
import com.kawannaik.model.enums.LevelFisik;

import java.time.LocalDate;
import java.util.List;

public interface RencanaPendakianService {
    RencanaPendakianResponse createRencana(String userEmail, RencanaPendakianRequest request);
    RencanaPendakianResponse getRencanaById(Long id);
    List<RencanaPendakianResponse> getAllRencana(Long gunungId, LevelFisik level, LocalDate tanggal);
    List<RencanaPendakianResponse> getMyRencana(String userEmail);
}
