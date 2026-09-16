package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Tabla del patrón Outbox (ver ADR-0002): garantiza publicación confiable de eventos. */
@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int version;

    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    @Column(name = "payload_json", nullable = false, length = 4000)
    private String payloadJson;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected OutboxEventEntity() {}

    public OutboxEventEntity(UUID eventId, String aggregateId, String type, int version, String correlationId,
                              String payloadJson, Instant occurredAt) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.type = type;
        this.version = version;
        this.correlationId = correlationId;
        this.payloadJson = payloadJson;
        this.occurredAt = occurredAt;
    }

    public UUID getEventId() { return eventId; }
    public String getAggregateId() { return aggregateId; }
    public String getType() { return type; }
    public int getVersion() { return version; }
    public String getCorrelationId() { return correlationId; }
    public String getPayloadJson() { return payloadJson; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public void marcarPublicado() { this.publishedAt = Instant.now(); }
}
