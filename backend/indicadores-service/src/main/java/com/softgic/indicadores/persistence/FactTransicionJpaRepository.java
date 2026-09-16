package com.softgic.indicadores.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FactTransicionJpaRepository extends JpaRepository<FactTransicionEntity, java.util.UUID> {

    @Query("SELECT f.estado AS estado, COUNT(f) AS total FROM FactTransicionEntity f GROUP BY f.estado")
    List<ConteoPorEstado> contarPorEstado();

    @Query("SELECT f.categoriaId AS categoriaId, COUNT(f) AS total FROM FactTransicionEntity f GROUP BY f.categoriaId")
    List<ConteoPorCategoria> contarPorCategoria();

    @Query("SELECT f.fecha AS fecha, COUNT(f) AS total FROM FactTransicionEntity f GROUP BY f.fecha ORDER BY f.fecha")
    List<ConteoPorFecha> tendenciaDiaria();

    interface ConteoPorEstado { String getEstado(); long getTotal(); }
    interface ConteoPorCategoria { java.util.UUID getCategoriaId(); long getTotal(); }
    interface ConteoPorFecha { LocalDate getFecha(); long getTotal(); }
}
