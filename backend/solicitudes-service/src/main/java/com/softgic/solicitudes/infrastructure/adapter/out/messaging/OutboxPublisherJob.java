package com.softgic.solicitudes.infrastructure.adapter.out.messaging;

import com.softgic.solicitudes.infrastructure.adapter.out.persistence.OutboxEventEntity;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.OutboxEventJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Poller del patrón Outbox: publica a Kafka los eventos aún no enviados y marca
 * published_at solo tras la confirmación del broker, evitando pérdida de eventos.
 * Evolución sugerida: reemplazar por CDC (Debezium) para menor latencia.
 */
@Component
public class OutboxPublisherJob {

    private final OutboxEventJpaRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public OutboxPublisherJob(OutboxEventJpaRepository outboxRepository,
                               KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${kafka.topic.solicitudes-eventos:solicitudes.eventos}") String topic) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:1000}")
    @Transactional
    public void publicarPendientes() {
        List<OutboxEventEntity> pendientes = outboxRepository.findTop50ByPublishedAtIsNullOrderByOccurredAtAsc();
        for (OutboxEventEntity evento : pendientes) {
            kafkaTemplate.send(topic, evento.getAggregateId(), evento.getPayloadJson())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            evento.marcarPublicado();
                            outboxRepository.save(evento);
                        }
                    });
        }
    }
}
