package com.softgic.solicitudes.infrastructure.adapter.in.rest;

import com.softgic.solicitudes.infrastructure.adapter.in.rest.dto.SolicitudResponse;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.SolicitudEntity;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.SolicitudJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Consultas de solo lectura para la bandeja y el detalle. Se mantiene fuera de los
 * casos de uso de escritura porque no forma parte del ciclo transaccional de negocio.
 */
@Service
public class SolicitudQueryService {

    private final SolicitudJpaRepository jpaRepository;

    public SolicitudQueryService(SolicitudJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Page<SolicitudResponse> buscar(String estado, int page, int size, Authentication authentication) {
        Specification<SolicitudEntity> spec = Specification.where(null);

        if (estado != null && !estado.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }

        UUID actorId = actorId(authentication);
        if (actorId != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SOLICITANTE"))) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("solicitanteId"), actorId));
        }

        Page<SolicitudEntity> pageResult = jpaRepository.findAll(spec, PageRequest.of(page, size));
        return pageResult.map(this::toResponse);
    }

    public SolicitudResponse obtenerDetalle(UUID id) {
        SolicitudEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada: " + id));
        return toResponse(entity);
    }

    private SolicitudResponse toResponse(SolicitudEntity e) {
        return new SolicitudResponse(e.getId(), e.getCodigo(), e.getAsunto(), e.getDescripcion(),
                e.getCategoriaId(), e.getPrioridad(), e.getEstado(), e.getSolicitanteId(), e.getAnalistaId());
    }

    private static UUID actorId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            try {
                return UUID.fromString(jwt.getSubject());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }
}
