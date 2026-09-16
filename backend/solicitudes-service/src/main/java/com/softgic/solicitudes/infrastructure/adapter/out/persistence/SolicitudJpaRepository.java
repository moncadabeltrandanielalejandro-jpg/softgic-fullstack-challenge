package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SolicitudJpaRepository extends JpaRepository<SolicitudEntity, UUID>, JpaSpecificationExecutor<SolicitudEntity> {

    /**
     * Asignación atómica: solo actualiza si sigue REGISTRADA y sin analista.
     * El valor de retorno (filas afectadas) determina quién "gana" la carrera (cubre A2).
     */
    @Modifying
    @Query("UPDATE SolicitudEntity s SET s.estado = 'EN_ATENCION', s.analistaId = :analistaId, " +
            "s.actualizadoEn = CURRENT_TIMESTAMP, s.version = s.version + 1 " +
            "WHERE s.id = :solicitudId AND s.estado = 'REGISTRADA' AND s.analistaId IS NULL")
    int asignarSiDisponible(@Param("solicitudId") UUID solicitudId, @Param("analistaId") UUID analistaId);
}
