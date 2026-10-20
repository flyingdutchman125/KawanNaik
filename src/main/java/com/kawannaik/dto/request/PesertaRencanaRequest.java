package com.kawannaik.dto.request;

import com.kawannaik.model.enums.StatusPermintaan;
import jakarta.validation.constraints.NotNull;

public class PesertaRencanaRequest {

    @NotNull(message = "Status permintaan tidak boleh kosong")
    private StatusPermintaan statusPermintaan;

    public PesertaRencanaRequest() {
        super();
    }

    public PesertaRencanaRequest(StatusPermintaan statusPermintaan) {
        super();
        this.statusPermintaan = statusPermintaan;
    }

    public StatusPermintaan getStatusPermintaan() {
        return statusPermintaan;
    }

    public void setStatusPermintaan(StatusPermintaan statusPermintaan) {
        this.statusPermintaan = statusPermintaan;
    }
}
