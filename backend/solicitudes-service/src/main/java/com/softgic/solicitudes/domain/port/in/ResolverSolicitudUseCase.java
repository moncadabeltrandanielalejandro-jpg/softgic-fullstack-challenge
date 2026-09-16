package com.softgic.solicitudes.domain.port.in;

import java.util.UUID;

public interface ResolverSolicitudUseCase {
    void ejecutar(UUID solicitudId, UUID analistaId, String observacion);
}
