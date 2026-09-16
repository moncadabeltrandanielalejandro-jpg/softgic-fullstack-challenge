package com.softgic.solicitudes.domain.port.in;

import java.util.UUID;

/** Acciones del Supervisor sobre una solicitud RESUELTA. */
public interface GestionarSupervisionUseCase {
    void devolverAAtencion(UUID solicitudId, UUID supervisorId, String motivo);

    void cerrar(UUID solicitudId, UUID supervisorId, String motivo);
}
