package com.softgic.solicitudes.domain.port.in;

import com.softgic.solicitudes.domain.model.Prioridad;
import com.softgic.solicitudes.domain.model.Solicitud;

import java.util.UUID;

public interface RegistrarSolicitudUseCase {

    record Comando(String asunto, String descripcion, UUID categoriaId, Prioridad prioridad, UUID solicitanteId) {}

    Solicitud ejecutar(Comando comando);
}
