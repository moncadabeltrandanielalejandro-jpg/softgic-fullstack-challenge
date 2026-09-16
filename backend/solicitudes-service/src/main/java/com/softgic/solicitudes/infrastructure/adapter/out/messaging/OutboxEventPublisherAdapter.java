package com.softgic.solicitudes.infrastructure.adapter.out.messaging;

import com.softgic.solicitudes.domain.event.DomainEvent;
import com.softgic.solicitudes.domain.port.out.EventPublisherPort;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.OutboxEventEntity;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.OutboxEventJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida del puerto de eventos: escribe en la tabla outbox dentro
 * de la transacción de negocio en curso. La publicación real a Kafka la hace
 * {@link OutboxPublisherJob} de forma asíncrona (ver ADR-0002).
 */
@Component
public class OutboxEventPublisherAdapter implements EventPublisherPort {

    private final OutboxEventJpaRepository outboxRepository;

    public OutboxEventPublisherAdapter(OutboxEventJpaRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    public void publicar(DomainEvent event) {
        outboxRepository.save(new OutboxEventEntity(
                event.eventId(), event.aggregateId(), event.type(), event.version(),
                event.correlationId(), event.payloadJson(), event.occurredAt()));
    }
}
