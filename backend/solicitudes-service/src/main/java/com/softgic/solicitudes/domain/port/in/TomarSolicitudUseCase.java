package com.softgic.solicitudes.domain.port.in;

import java.util.UUID;

public interface TomarSolicitudUseCase {
    void ejecutar(UUID solicitudId, UUID analistaId);
}
