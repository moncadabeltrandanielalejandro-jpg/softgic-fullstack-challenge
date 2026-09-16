package com.softgic.solicitudes.domain.port.out;

import com.softgic.solicitudes.domain.model.Solicitud;

import java.util.Optional;
import java.util.UUID;

/** Puerto de salida hacia la persistencia; el dominio no conoce JPA. */
public interface SolicitudRepositoryPort {

    Solicitud guardar(Solicitud solicitud);

    Optional<Solicitud> buscarPorId(UUID id);

    /**
     * Intenta asignar atómicamente un analista solo si la solicitud sigue en REGISTRADA
     * y sin analista asignado. Devuelve true si esta llamada ganó la asignación.
     * Implementación típica: UPDATE ... WHERE estado='REGISTRADA' AND analista_id IS NULL.
     */
    boolean asignarAtomicamente(UUID solicitudId, UUID analistaId);
}
