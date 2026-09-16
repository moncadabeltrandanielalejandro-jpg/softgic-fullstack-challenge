package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historial_estados")
public class HistorialEstadoEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "solicitud_id", nullable = false)
    private UUID solicitudId;

    @Column(name = "estado_origen")
    private String estadoOrigen;

    @Column(name = "estado_destino", nullable = false)
    private String estadoDestino;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Column(name = "rol_actor", nullable = false)
    private String rolActor;

    private String motivo;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    protected HistorialEstadoEntity() {}

    public HistorialEstadoEntity(UUID solicitudId, String estadoOrigen, String estadoDestino, UUID actorId,
                                  String rolActor, String motivo, Instant ocurridoEn) {
        this.solicitudId = solicitudId;
        this.estadoOrigen = estadoOrigen;
        this.estadoDestino = estadoDestino;
        this.actorId = actorId;
        this.rolActor = rolActor;
        this.motivo = motivo;
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getId() { return id; }
    public UUID getSolicitudId() { return solicitudId; }
    public String getEstadoOrigen() { return estadoOrigen; }
    public String getEstadoDestino() { return estadoDestino; }
    public UUID getActorId() { return actorId; }
    public String getRolActor() { return rolActor; }
    public String getMotivo() { return motivo; }
    public Instant getOcurridoEn() { return ocurridoEn; }
}
