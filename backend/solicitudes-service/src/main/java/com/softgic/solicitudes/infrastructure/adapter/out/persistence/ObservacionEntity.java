package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "observaciones")
public class ObservacionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "solicitud_id", nullable = false)
    private UUID solicitudId;

    @Column(name = "autor_id", nullable = false)
    private UUID autorId;

    @Column(nullable = false, length = 2000)
    private String texto;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    protected ObservacionEntity() {}

    public ObservacionEntity(UUID solicitudId, UUID autorId, String texto, Instant creadoEn) {
        this.solicitudId = solicitudId;
        this.autorId = autorId;
        this.texto = texto;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public UUID getSolicitudId() { return solicitudId; }
    public UUID getAutorId() { return autorId; }
    public String getTexto() { return texto; }
    public Instant getCreadoEn() { return creadoEn; }
}
