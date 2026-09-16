package com.softgic.solicitudes.infrastructure.adapter.in.rest.dto;

import com.softgic.solicitudes.domain.model.Solicitud;

import java.util.UUID;

public record SolicitudResponse(
        UUID id,
        String codigo,
        String asunto,
        String descripcion,
        UUID categoriaId,
        String prioridad,
        String estado,
        UUID solicitanteId,
        UUID analistaId
) {
    public static SolicitudResponse from(Solicitud s) {
        return new SolicitudResponse(s.getId(), s.getCodigo(), s.getAsunto(), s.getDescripcion(),
                s.getCategoriaId(), s.getPrioridad().name(), s.getEstado().name(), s.getSolicitanteId(),
                s.getAnalistaId());
    }
}
