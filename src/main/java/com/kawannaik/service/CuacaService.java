package com.kawannaik.service;

import com.kawannaik.dto.response.CuacaResponse;

public interface CuacaService {
    CuacaResponse getCuacaByGunung(Long gunungId);
}
