package com.softgic.solicitudes.infrastructure.adapter.in.rest;

import com.softgic.solicitudes.domain.model.SolicitudYaAsignadaException;
import com.softgic.solicitudes.domain.model.TransicionInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorResponse(String codigo, String mensaje, Instant timestamp) {
        static ErrorResponse of(String codigo, String mensaje) {
            return new ErrorResponse(codigo, mensaje, Instant.now());
        }
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionInvalida(TransicionInvalidaException ex) {
        // A4: transición de dominio rechazada con respuesta explicativa.
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of("TRANSICION_INVALIDA", ex.getMessage()));
    }

    @ExceptionHandler(SolicitudYaAsignadaException.class)
    public ResponseEntity<ErrorResponse> handleYaAsignada(SolicitudYaAsignadaException ex) {
        // A2: la solicitud ya fue tomada por otro analista.
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("SOLICITUD_YA_ASIGNADA", ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("SOLICITUD_INVALIDA", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Payload inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of("VALIDACION", mensaje));
    }
}
