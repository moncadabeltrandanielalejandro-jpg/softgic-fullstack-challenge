package com.softgic.indicadores.rest;

import com.softgic.indicadores.persistence.FactTransicionJpaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/indicadores")
@Tag(name = "Indicadores")
public class IndicadoresController {

    private final FactTransicionJpaRepository factRepository;

    public IndicadoresController(FactTransicionJpaRepository factRepository) {
        this.factRepository = factRepository;
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('ANALISTA','SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> resumen() {
        return ResponseEntity.ok(Map.of(
                "porEstado", factRepository.contarPorEstado(),
                "porCategoria", factRepository.contarPorCategoria()
        ));
    }

    @GetMapping("/tendencia")
    @PreAuthorize("hasAnyRole('ANALISTA','SUPERVISOR')")
    public ResponseEntity<?> tendencia() {
        return ResponseEntity.ok(factRepository.tendenciaDiaria());
    }
}
