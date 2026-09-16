package com.softgic.solicitudes.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgic.solicitudes.domain.event.DomainEvent;
import com.softgic.solicitudes.domain.model.Solicitud;
import com.softgic.solicitudes.domain.port.in.GestionarSupervisionUseCase;
import com.softgic.solicitudes.domain.port.out.EventPublisherPort;
import com.softgic.solicitudes.domain.port.out.SolicitudRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class GestionarSupervisionService implements GestionarSupervisionUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final EventPublisherPort eventPublisher;
    private final ObjectMapper objectMapper;

    public GestionarSupervisionService(SolicitudRepositoryPort solicitudRepository, EventPublisherPort eventPublisher,
                                        ObjectMapper objectMapper) {
        this.solicitudRepository = solicitudRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void devolverAAtencion(UUID solicitudId, UUID supervisorId, String motivo) {
        Solicitud solicitud = obtener(solicitudId);
        solicitud.devolverAAtencion(supervisorId, motivo);
        solicitudRepository.guardar(solicitud);
        publicar("SolicitudDevueltaAAtencion", solicitudId, supervisorId, motivo);
    }

    @Override
    @Transactional
    public void cerrar(UUID solicitudId, UUID supervisorId, String motivo) {
        Solicitud solicitud = obtener(solicitudId);
        solicitud.cerrar(supervisorId, motivo);
        solicitudRepository.guardar(solicitud);
        publicar("SolicitudCerrada", solicitudId, supervisorId, motivo);
    }

    private Solicitud obtener(UUID solicitudId) {
        return solicitudRepository.buscarPorId(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada: " + solicitudId));
    }

    private void publicar(String tipo, UUID solicitudId, UUID actorId, String motivo) {
        try {
            String payload = objectMapper.writeValueAsString(new SupervisionPayload(
                    solicitudId.toString(), actorId.toString(), motivo));
            eventPublisher.publicar(DomainEvent.of(tipo, solicitudId.toString(), UUID.randomUUID().toString(), payload));
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible serializar el evento " + tipo, e);
        }
    }

    private record SupervisionPayload(String solicitudId, String actorId, String motivo) {}
}
