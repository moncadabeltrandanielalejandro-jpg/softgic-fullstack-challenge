package com.softgic.solicitudes;

import com.softgic.solicitudes.domain.event.DomainEvent;
import com.softgic.solicitudes.infrastructure.adapter.in.rest.SolicitudQueryService;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.SolicitudEntity;
import com.softgic.solicitudes.infrastructure.adapter.out.persistence.SolicitudJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixesRegressionTest {

    @Mock
    private SolicitudJpaRepository jpaRepository;

    @Test
    void domainEvent_canUseExplicitEventId() {
        UUID eventId = UUID.randomUUID();

        DomainEvent event = DomainEvent.of("SolicitudRegistrada", "solicitud-1", "corr-1", "{}", eventId);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.type()).isEqualTo("SolicitudRegistrada");
    }

    @Test
    void buscar_paraSolicitante_filtraPorSolicitanteYEstado() {
        UUID actorId = UUID.randomUUID();
        Authentication authentication = mockAuthentication(actorId, "ROLE_SOLICITANTE");

        SolicitudEntity entity = new SolicitudEntity(
                UUID.randomUUID(), "SOL-2026-000001", "Asunto", "Descripcion",
                UUID.randomUUID(), "MEDIA", "REGISTRADA", actorId, null,
                java.time.Instant.now(), java.time.Instant.now(), 0L);
        Page<SolicitudEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
        when(jpaRepository.findAll(any(Specification.class), eq(PageRequest.of(0, 20)))).thenReturn(page);

        SolicitudQueryService service = new SolicitudQueryService(jpaRepository);
        service.buscar("REGISTRADA", 0, 20, authentication);

        verify(jpaRepository).findAll(any(Specification.class), eq(PageRequest.of(0, 20)));
    }

    private static Authentication mockAuthentication(UUID actorId, String role) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", actorId.toString())
                .build();

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        org.mockito.Mockito.when(authentication.getPrincipal()).thenReturn(jwt);
        doReturn(List.of((GrantedAuthority) new SimpleGrantedAuthority(role)))
            .when(authentication).getAuthorities();
        return authentication;
    }
}
