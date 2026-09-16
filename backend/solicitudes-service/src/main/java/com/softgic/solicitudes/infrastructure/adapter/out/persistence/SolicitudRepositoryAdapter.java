package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import com.softgic.solicitudes.domain.model.EstadoSolicitud;
import com.softgic.solicitudes.domain.model.Prioridad;
import com.softgic.solicitudes.domain.model.Solicitud;
import com.softgic.solicitudes.domain.port.out.SolicitudRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class SolicitudRepositoryAdapter implements SolicitudRepositoryPort {

    private final SolicitudJpaRepository jpaRepository;

    public SolicitudRepositoryAdapter(SolicitudJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Solicitud guardar(Solicitud solicitud) {
        Instant ahora = Instant.now();
        SolicitudEntity existente = jpaRepository.findById(solicitud.getId()).orElse(null);
        Instant creadoEn = existente != null ? existente.getCreadoEn() : ahora;
        long version = existente != null ? existente.getVersion() : solicitud.getVersion();

        SolicitudEntity entity = new SolicitudEntity(
                solicitud.getId(), solicitud.getCodigo(), solicitud.getAsunto(), solicitud.getDescripcion(),
                solicitud.getCategoriaId(), solicitud.getPrioridad().name(), solicitud.getEstado().name(),
            solicitud.getSolicitanteId(), solicitud.getAnalistaId(), creadoEn, ahora, version);

        SolicitudEntity guardada = jpaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<Solicitud> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean asignarAtomicamente(UUID solicitudId, UUID analistaId) {
        return jpaRepository.asignarSiDisponible(solicitudId, analistaId) == 1;
    }

    // NOTA: para el alcance de esta entrega, el historial completo y las observaciones se
    // reconstruyen a través de las tablas historial_estados/observaciones consultadas aparte
    // (ver SolicitudQueryAdapter); reconstruir() aquí solo trae el estado transaccional actual.
    private Solicitud toDomain(SolicitudEntity entity) {
        return Solicitud.reconstruir(
                entity.getId(), entity.getCodigo(), entity.getAsunto(), entity.getDescripcion(),
                entity.getCategoriaId(), Prioridad.valueOf(entity.getPrioridad()), entity.getSolicitanteId(),
                EstadoSolicitud.valueOf(entity.getEstado()), entity.getAnalistaId(), entity.getVersion());
    }
}
