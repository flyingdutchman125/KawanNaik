package com.kawannaik.dto.request;

import com.kawannaik.model.enums.StatusRencana;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRencanaRequest {

    @NotNull(message = "Status rencana tidak boleh kosong")
    private StatusRencana status;

    public UpdateStatusRencanaRequest() {
        super();
    }

    public UpdateStatusRencanaRequest(StatusRencana status) {
        super();
        this.status = status;
    }

    public StatusRencana getStatus() {
        return status;
    }

    public void setStatus(StatusRencana status) {
        this.status = status;
    }
}
