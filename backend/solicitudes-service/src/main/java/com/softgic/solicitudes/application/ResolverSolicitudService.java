package com.softgic.solicitudes.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgic.solicitudes.domain.event.DomainEvent;
import com.softgic.solicitudes.domain.model.Solicitud;
import com.softgic.solicitudes.domain.port.in.ResolverSolicitudUseCase;
import com.softgic.solicitudes.domain.port.out.EventPublisherPort;
import com.softgic.solicitudes.domain.port.out.SolicitudRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ResolverSolicitudService implements ResolverSolicitudUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final EventPublisherPort eventPublisher;
    private final ObjectMapper objectMapper;

    public ResolverSolicitudService(SolicitudRepositoryPort solicitudRepository, EventPublisherPort eventPublisher,
                                     ObjectMapper objectMapper) {
        this.solicitudRepository = solicitudRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void ejecutar(UUID solicitudId, UUID analistaId, String observacion) {
        Solicitud solicitud = solicitudRepository.buscarPorId(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada: " + solicitudId));

        // Lanza TransicionInvalidaException si no está en EN_ATENCION (cubre A4 en variantes análogas).
        solicitud.resolver(analistaId, observacion);
        solicitudRepository.guardar(solicitud);

        try {
            String payload = objectMapper.writeValueAsString(new SolicitudResueltaPayload(
                    solicitudId.toString(), analistaId.toString(), observacion));
            eventPublisher.publicar(DomainEvent.of("SolicitudResuelta", solicitudId.toString(),
                    UUID.randomUUID().toString(), payload));
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible serializar el evento SolicitudResuelta", e);
        }
    }

    private record SolicitudResueltaPayload(String solicitudId, String analistaId, String observacion) {}
}
