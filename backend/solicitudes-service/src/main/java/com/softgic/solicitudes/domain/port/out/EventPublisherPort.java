package com.softgic.solicitudes.domain.port.out;

import com.softgic.solicitudes.domain.event.DomainEvent;

/**
 * Puerto de salida para eventos de dominio. La implementación de infraestructura
 * escribe en la tabla outbox dentro de la misma transacción de negocio (ver ADR-0002).
 */
public interface EventPublisherPort {
    void publicar(DomainEvent event);
}
