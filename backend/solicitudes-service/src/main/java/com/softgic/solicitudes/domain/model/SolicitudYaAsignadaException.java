package com.softgic.solicitudes.domain.model;

/** Se intenta tomar una solicitud que ya fue asignada (cubre escenario A2). */
public class SolicitudYaAsignadaException extends RuntimeException {

    public SolicitudYaAsignadaException(String solicitudId) {
        super("La solicitud %s ya fue asignada a otro analista".formatted(solicitudId));
    }
}
