package com.softgic.solicitudes.domain.event;

import java.time.Instant;
import java.util.UUID;

/** Sobre común de todos los eventos de dominio publicados por el servicio. */
public record DomainEvent(
        UUID eventId,
        String type,
        int version,
        String aggregateId,
        String correlationId,
        Instant occurredAt,
        String payloadJson
) {
    public static DomainEvent of(String type, String aggregateId, String correlationId, String payloadJson) {
        return of(type, aggregateId, correlationId, payloadJson, UUID.randomUUID());
    }

    public static DomainEvent of(String type, String aggregateId, String correlationId, String payloadJson,
                                 UUID eventId) {
        return new DomainEvent(eventId, type, 1, aggregateId, correlationId, Instant.now(), payloadJson);
    }
}
