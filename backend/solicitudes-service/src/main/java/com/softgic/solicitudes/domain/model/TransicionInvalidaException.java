package com.softgic.solicitudes.domain.model;

/** Excepción de dominio para transiciones de estado no permitidas (cubre escenario A4). */
public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(EstadoSolicitud origen, EstadoSolicitud destino) {
        super("Transición no permitida de %s a %s".formatted(origen, destino));
    }
}
