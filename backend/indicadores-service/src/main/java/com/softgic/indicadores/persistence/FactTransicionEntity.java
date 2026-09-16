package com.softgic.indicadores.persistence;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/** Tabla de hechos del esquema estrella: una fila por transición de estado. */
@Entity
@Table(name = "fact_transiciones")
public class FactTransicionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "solicitud_id", nullable = false)
    private UUID solicitudId;

    @Column(name = "categoria_id")
    private UUID categoriaId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha")
    private LocalDate fecha;

    protected FactTransicionEntity() {}

    public FactTransicionEntity(UUID solicitudId, UUID categoriaId, String estado, LocalDate fecha) {
        this.solicitudId = solicitudId;
        this.categoriaId = categoriaId;
        this.estado = estado;
        this.fecha = fecha;
    }

    public UUID getId() { return id; }
    public UUID getSolicitudId() { return solicitudId; }
    public UUID getCategoriaId() { return categoriaId; }
    public String getEstado() { return estado; }
    public LocalDate getFecha() { return fecha; }
}
