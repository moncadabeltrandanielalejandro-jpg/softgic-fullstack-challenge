package com.softgic.solicitudes.infrastructure.adapter.in.rest.dto;

import com.softgic.solicitudes.domain.model.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CrearSolicitudRequest(
        @NotBlank @Size(max = 200) String asunto,
        @NotBlank @Size(max = 2000) String descripcion,
        @NotNull UUID categoriaId,
        @NotNull Prioridad prioridad
) {}
