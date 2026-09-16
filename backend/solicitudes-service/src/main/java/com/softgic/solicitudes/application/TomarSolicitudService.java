package com.softgic.solicitudes.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgic.solicitudes.domain.event.DomainEvent;
import com.softgic.solicitudes.domain.model.Solicitud;
import com.softgic.solicitudes.domain.model.SolicitudYaAsignadaException;
import com.softgic.solicitudes.domain.port.in.TomarSolicitudUseCase;
import com.softgic.solicitudes.domain.port.out.EventPublisherPort;
import com.softgic.solicitudes.domain.port.out.SolicitudRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TomarSolicitudService implements TomarSolicitudUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final EventPublisherPort eventPublisher;
    private final ObjectMapper objectMapper;

    public TomarSolicitudService(SolicitudRepositoryPort solicitudRepository, EventPublisherPort eventPublisher,
                                  ObjectMapper objectMapper) {
        this.solicitudRepository = solicitudRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void ejecutar(UUID solicitudId, UUID analistaId) {
        // Asignación atómica a nivel de UPDATE: solo una llamada concurrente puede ganar (cubre A2).
        boolean gano = solicitudRepository.asignarAtomicamente(solicitudId, analistaId);
        if (!gano) {
            throw new SolicitudYaAsignadaException(solicitudId.toString());
        }

        Solicitud solicitud = solicitudRepository.buscarPorId(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada: " + solicitudId));

        try {
            String payload = objectMapper.writeValueAsString(new SolicitudTomadaPayload(
                    solicitudId.toString(), analistaId.toString(), solicitud.getEstado().name()));
            eventPublisher.publicar(DomainEvent.of("SolicitudTomada", solicitudId.toString(),
                    UUID.randomUUID().toString(), payload));
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible serializar el evento SolicitudTomada", e);
        }
    }

    private record SolicitudTomadaPayload(String solicitudId, String analistaId, String estado) {}
}
