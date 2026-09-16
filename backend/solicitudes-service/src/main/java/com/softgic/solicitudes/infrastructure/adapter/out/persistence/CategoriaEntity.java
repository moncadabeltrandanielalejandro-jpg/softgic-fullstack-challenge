package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categorias")
public class CategoriaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean activa;

    protected CategoriaEntity() {}

    public CategoriaEntity(UUID id, String nombre, boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.activa = activa;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isActiva() { return activa; }
}
