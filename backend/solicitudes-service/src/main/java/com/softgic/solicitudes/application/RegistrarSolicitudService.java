package com.softgic.solicitudes.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgic.solicitudes.domain.event.DomainEvent;
import com.softgic.solicitudes.domain.model.Solicitud;
import com.softgic.solicitudes.domain.port.in.RegistrarSolicitudUseCase;
import com.softgic.solicitudes.domain.port.out.CategoriaRepositoryPort;
import com.softgic.solicitudes.domain.port.out.EventPublisherPort;
import com.softgic.solicitudes.domain.port.out.SolicitudRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RegistrarSolicitudService implements RegistrarSolicitudUseCase {

    private static final AtomicLong SECUENCIA = new AtomicLong(1);

    private final SolicitudRepositoryPort solicitudRepository;
    private final CategoriaRepositoryPort categoriaRepository;
    private final EventPublisherPort eventPublisher;
    private final ObjectMapper objectMapper;

    public RegistrarSolicitudService(SolicitudRepositoryPort solicitudRepository,
                                      CategoriaRepositoryPort categoriaRepository,
                                      EventPublisherPort eventPublisher,
                                      ObjectMapper objectMapper) {
        this.solicitudRepository = solicitudRepository;
        this.categoriaRepository = categoriaRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Solicitud ejecutar(Comando comando) {
        if (!categoriaRepository.existeActiva(comando.categoriaId())) {
            throw new IllegalArgumentException("Categoría inexistente o inactiva: " + comando.categoriaId());
        }

        String codigo = generarCodigoLegible();
        Solicitud solicitud = Solicitud.registrar(codigo, comando.asunto(), comando.descripcion(),
                comando.categoriaId(), comando.prioridad(), comando.solicitanteId());

        Solicitud guardada = solicitudRepository.guardar(solicitud);

        // El evento se escribe en la tabla outbox dentro de la misma transacción (ADR-0002):
        // solo se publica a Kafka después de confirmado el commit.
        eventPublisher.publicar(construirEvento(guardada));

        return guardada;
    }

    private DomainEvent construirEvento(Solicitud s) {
        try {
            String payload = objectMapper.writeValueAsString(new SolicitudRegistradaPayload(
                    s.getId().toString(), s.getCodigo(), s.getCategoriaId().toString(),
                    s.getPrioridad().name(), s.getEstado().name(), s.getSolicitanteId().toString()));
            return DomainEvent.of("SolicitudRegistrada", s.getId().toString(), UUID.randomUUID().toString(), payload);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible serializar el evento SolicitudRegistrada", e);
        }
    }

    private static String generarCodigoLegible() {
        return "SOL-%d-%06d".formatted(java.time.Year.now().getValue(), SECUENCIA.getAndIncrement());
    }

    private record SolicitudRegistradaPayload(String solicitudId, String codigo, String categoriaId,
                                               String prioridad, String estado, String solicitanteId) {}
}
