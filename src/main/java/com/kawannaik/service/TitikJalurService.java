package com.kawannaik.service;

import com.kawannaik.dto.request.TitikJalurRequest;
import com.kawannaik.dto.response.TitikJalurResponse;

import java.util.List;

public interface TitikJalurService {
    List<TitikJalurResponse> getTitikByJalurId(Long jalurId);
    TitikJalurResponse createTitikJalur(TitikJalurRequest request);
    TitikJalurResponse updateTitikJalur(Long id, TitikJalurRequest request);
    void deleteTitikJalur(Long id);
}
