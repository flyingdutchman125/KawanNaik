package com.kawannaik.service;

import com.kawannaik.dto.request.JalurRequest;
import com.kawannaik.dto.response.JalurResponse;

import java.util.List;

public interface JalurService {
    List<JalurResponse> getJalurByGunungId(Long gunungId);
    JalurResponse getJalurById(Long id);
    JalurResponse createJalur(JalurRequest request);
    JalurResponse updateJalur(Long id, JalurRequest request);
    void deleteJalur(Long id);
}
