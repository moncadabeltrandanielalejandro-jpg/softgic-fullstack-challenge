package com.softgic.solicitudes.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Agregado raíz. Encapsula la máquina de estados y garantiza que solo se
 * apliquen transiciones válidas, dejando historial de cada cambio.
 */
public class Solicitud {

    private final UUID id;
    private final String codigo;
    private final String asunto;
    private final String descripcion;
    private final UUID categoriaId;
    private final Prioridad prioridad;
    private final UUID solicitanteId;
    private EstadoSolicitud estado;
    private UUID analistaId;
    private final List<HistorialEstado> historial = new ArrayList<>();
    private final List<Observacion> observaciones = new ArrayList<>();
    private long version;

    private Solicitud(UUID id, String codigo, String asunto, String descripcion, UUID categoriaId,
                       Prioridad prioridad, UUID solicitanteId, EstadoSolicitud estado, UUID analistaId,
                       long version) {
        this.id = id;
        this.codigo = codigo;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.prioridad = prioridad;
        this.solicitanteId = solicitanteId;
        this.estado = estado;
        this.analistaId = analistaId;
        this.version = version;
    }

    /** Fábrica para el registro de una nueva solicitud (siempre inicia en REGISTRADA). */
    public static Solicitud registrar(String codigo, String asunto, String descripcion, UUID categoriaId,
                                       Prioridad prioridad, UUID solicitanteId) {
        Solicitud solicitud = new Solicitud(UUID.randomUUID(), codigo, asunto, descripcion, categoriaId,
                prioridad, solicitanteId, EstadoSolicitud.REGISTRADA, null, 0L);
        solicitud.historial.add(HistorialEstado.crear(null, EstadoSolicitud.REGISTRADA, solicitanteId,
                Rol.SOLICITANTE, "Registro inicial"));
        return solicitud;
    }

    /** Reconstrucción desde persistencia; no dispara eventos ni valida transición. */
    public static Solicitud reconstruir(UUID id, String codigo, String asunto, String descripcion, UUID categoriaId,
                                         Prioridad prioridad, UUID solicitanteId, EstadoSolicitud estado,
                                         UUID analistaId, long version) {
        return new Solicitud(id, codigo, asunto, descripcion, categoriaId, prioridad, solicitanteId, estado,
                analistaId, version);
    }

    public void tomar(UUID analistaId) {
        exigirTransicionPermitida(EstadoSolicitud.EN_ATENCION);
        this.analistaId = analistaId;
        this.estado = EstadoSolicitud.EN_ATENCION;
        historial.add(HistorialEstado.crear(EstadoSolicitud.REGISTRADA, EstadoSolicitud.EN_ATENCION, analistaId,
                Rol.ANALISTA, "Toma de solicitud"));
        version++;
    }

    public void resolver(UUID analistaId, String observacionTexto) {
        exigirTransicionPermitida(EstadoSolicitud.RESUELTA);
        this.estado = EstadoSolicitud.RESUELTA;
        observaciones.add(Observacion.crear(analistaId, observacionTexto));
        historial.add(HistorialEstado.crear(EstadoSolicitud.EN_ATENCION, EstadoSolicitud.RESUELTA, analistaId,
                Rol.ANALISTA, "Resolución"));
        version++;
    }

    public void devolverAAtencion(UUID supervisorId, String motivo) {
        exigirTransicionPermitida(EstadoSolicitud.EN_ATENCION);
        EstadoSolicitud origen = this.estado;
        this.estado = EstadoSolicitud.EN_ATENCION;
        historial.add(HistorialEstado.crear(origen, EstadoSolicitud.EN_ATENCION, supervisorId, Rol.SUPERVISOR, motivo));
        version++;
    }

    public void cerrar(UUID supervisorId, String motivo) {
        exigirTransicionPermitida(EstadoSolicitud.CERRADA);
        EstadoSolicitud origen = this.estado;
        this.estado = EstadoSolicitud.CERRADA;
        historial.add(HistorialEstado.crear(origen, EstadoSolicitud.CERRADA, supervisorId, Rol.SUPERVISOR, motivo));
        version++;
    }

    /** Reglas de la máquina de estados: única fuente de verdad sobre saltos permitidos. */
    private void exigirTransicionPermitida(EstadoSolicitud destino) {
        boolean permitido = switch (estado) {
            case REGISTRADA -> destino == EstadoSolicitud.EN_ATENCION;
            case EN_ATENCION -> destino == EstadoSolicitud.RESUELTA;
            case RESUELTA -> destino == EstadoSolicitud.EN_ATENCION || destino == EstadoSolicitud.CERRADA;
            case CERRADA -> false;
        };
        if (!permitido) {
            throw new TransicionInvalidaException(estado, destino);
        }
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getAsunto() { return asunto; }
    public String getDescripcion() { return descripcion; }
    public UUID getCategoriaId() { return categoriaId; }
    public Prioridad getPrioridad() { return prioridad; }
    public UUID getSolicitanteId() { return solicitanteId; }
    public EstadoSolicitud getEstado() { return estado; }
    public UUID getAnalistaId() { return analistaId; }
    public List<HistorialEstado> getHistorial() { return List.copyOf(historial); }
    public List<Observacion> getObservaciones() { return List.copyOf(observaciones); }
    public long getVersion() { return version; }

    public record HistorialEstado(EstadoSolicitud estadoOrigen, EstadoSolicitud estadoDestino, UUID actorId,
                                   Rol rolActor, String motivo, Instant ocurridoEn) {
        static HistorialEstado crear(EstadoSolicitud origen, EstadoSolicitud destino, UUID actorId, Rol rol,
                                      String motivo) {
            return new HistorialEstado(origen, destino, actorId, rol, motivo, Instant.now());
        }
    }

    public record Observacion(UUID autorId, String texto, Instant creadoEn) {
        static Observacion crear(UUID autorId, String texto) {
            return new Observacion(autorId, texto, Instant.now());
        }
    }
}
