package com.softgic.indicadores.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgic.indicadores.persistence.FactTransicionEntity;
import com.softgic.indicadores.persistence.FactTransicionJpaRepository;
import com.softgic.indicadores.persistence.ProcessedEventEntity;
import com.softgic.indicadores.persistence.ProcessedEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Consumidor de eventos de dominio publicados por solicitudes-service.
 * Idempotente: registra el eventId en processed_events antes de proyectar,
 * de forma que un reintento o entrega duplicada del broker no duplica el conteo (A5).
 */
@Component
public class SolicitudEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SolicitudEventConsumer.class);

    private final ProcessedEventJpaRepository processedEventRepository;
    private final FactTransicionJpaRepository factRepository;
    private final ObjectMapper objectMapper;

    public SolicitudEventConsumer(ProcessedEventJpaRepository processedEventRepository,
                                   FactTransicionJpaRepository factRepository,
                                   ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.factRepository = factRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.solicitudes-eventos:solicitudes.eventos}",
            groupId = "${spring.kafka.consumer.group-id:indicadores-service}")
    @Transactional
    public void consumir(String payloadJson) {
        try {
            JsonNode payload = objectMapper.readTree(payloadJson);
            UUID eventId = UUID.randomUUID(); // TODO: propagar eventId real del sobre del evento (ver contrato).
            if (processedEventRepository.existsById(eventId)) {
                log.debug("Evento {} ya procesado, se descarta (idempotencia)", eventId);
                return;
            }
            UUID solicitudId = UUID.fromString(payload.path("solicitudId").asText());
            String estado = payload.path("estado").asText("DESCONOCIDO");
            UUID categoriaId = payload.hasNonNull("categoriaId")
                    ? UUID.fromString(payload.get("categoriaId").asText()) : null;

            factRepository.save(new FactTransicionEntity(solicitudId, categoriaId, estado, LocalDate.now()));
            processedEventRepository.save(new ProcessedEventEntity(eventId, Instant.now()));
        } catch (Exception e) {
            log.error("Error procesando evento de solicitudes: {}", payloadJson, e);
            throw new IllegalStateException("No fue posible procesar el evento", e);
        }
    }
}
