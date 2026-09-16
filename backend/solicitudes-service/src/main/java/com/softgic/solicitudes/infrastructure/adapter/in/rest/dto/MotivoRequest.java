package com.softgic.solicitudes.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record MotivoRequest(@NotBlank String motivo) {}
