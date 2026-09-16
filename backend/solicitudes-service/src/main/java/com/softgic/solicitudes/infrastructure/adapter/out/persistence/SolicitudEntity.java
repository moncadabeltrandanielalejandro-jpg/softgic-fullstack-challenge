package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "solicitudes")
public class SolicitudEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "categoria_id", nullable = false)
    private UUID categoriaId;

    @Column(nullable = false)
    private String prioridad;

    @Column(nullable = false)
    private String estado;

    @Column(name = "solicitante_id", nullable = false)
    private UUID solicitanteId;

    @Column(name = "analista_id")
    private UUID analistaId;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @Version
    @Column(nullable = false)
    private long version;

    @OneToMany(mappedBy = "solicitudId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistorialEstadoEntity> historial = new ArrayList<>();

    @OneToMany(mappedBy = "solicitudId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ObservacionEntity> observaciones = new ArrayList<>();

    protected SolicitudEntity() {}

    public SolicitudEntity(UUID id, String codigo, String asunto, String descripcion, UUID categoriaId,
                            String prioridad, String estado, UUID solicitanteId, UUID analistaId,
                            Instant creadoEn, Instant actualizadoEn, long version) {
        this.id = id;
        this.codigo = codigo;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.prioridad = prioridad;
        this.estado = estado;
        this.solicitanteId = solicitanteId;
        this.analistaId = analistaId;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.version = version;
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getAsunto() { return asunto; }
    public String getDescripcion() { return descripcion; }
    public UUID getCategoriaId() { return categoriaId; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public UUID getSolicitanteId() { return solicitanteId; }
    public UUID getAnalistaId() { return analistaId; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public long getVersion() { return version; }
    public List<HistorialEstadoEntity> getHistorial() { return historial; }
    public List<ObservacionEntity> getObservaciones() { return observaciones; }
}
