package com.softgic.indicadores.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Registro de idempotencia: evita duplicar proyecciones si el broker reentrega un evento (A5). */
@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedEventEntity() {}

    public ProcessedEventEntity(UUID eventId, Instant processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    public UUID getEventId() { return eventId; }
}
