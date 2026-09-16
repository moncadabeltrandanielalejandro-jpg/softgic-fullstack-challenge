package com.softgic.solicitudes.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudTest {

    @Test
    void registrar_creaEnEstadoRegistradaConHistorialInicial() {
        Solicitud solicitud = Solicitud.registrar("SOL-2026-000001", "Asunto", "Descripción",
                UUID.randomUUID(), Prioridad.MEDIA, UUID.randomUUID());

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA);
        assertThat(solicitud.getHistorial()).hasSize(1);
    }

    @Test
    void tomar_transicionaAEnAtencion() {
        Solicitud solicitud = registrarSolicitud();
        UUID analista = UUID.randomUUID();

        solicitud.tomar(analista);

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.EN_ATENCION);
        assertThat(solicitud.getAnalistaId()).isEqualTo(analista);
    }

    @Test
    void resolver_sinEstarEnAtencion_lanzaTransicionInvalida() {
        Solicitud solicitud = registrarSolicitud();

        assertThatThrownBy(() -> solicitud.resolver(UUID.randomUUID(), "obs"))
                .isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void cerrar_directamenteDesdeRegistrada_esRechazado() {
        // Cubre A4: no se permiten saltos arbitrarios (ej. RESUELTA -> REGISTRADA / REGISTRADA -> CERRADA).
        Solicitud solicitud = registrarSolicitud();

        assertThatThrownBy(() -> solicitud.cerrar(UUID.randomUUID(), "motivo"))
                .isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void flujoCompleto_registrar_tomar_resolver_cerrar() {
        Solicitud solicitud = registrarSolicitud();
        UUID analista = UUID.randomUUID();
        UUID supervisor = UUID.randomUUID();

        solicitud.tomar(analista);
        solicitud.resolver(analista, "Resuelto correctamente");
        solicitud.cerrar(supervisor, "Cierre conforme");

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.CERRADA);
        assertThat(solicitud.getHistorial()).hasSize(4);
        assertThat(solicitud.getObservaciones()).hasSize(1);
    }

    private static Solicitud registrarSolicitud() {
        return Solicitud.registrar("SOL-2026-000001", "Asunto", "Descripción", UUID.randomUUID(),
                Prioridad.ALTA, UUID.randomUUID());
    }
}
