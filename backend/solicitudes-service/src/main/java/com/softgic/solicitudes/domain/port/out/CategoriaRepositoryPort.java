package com.softgic.solicitudes.domain.port.out;

import java.util.List;
import java.util.UUID;

/** Puerto de solo lectura hacia el catálogo de categorías. */
public interface CategoriaRepositoryPort {

    record CategoriaDto(UUID id, String nombre, boolean activa) {}

    List<CategoriaDto> listarActivas();

    boolean existeActiva(UUID categoriaId);
}
