package com.softgic.solicitudes.infrastructure.adapter.in.rest;

import com.softgic.solicitudes.domain.port.out.CategoriaRepositoryPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías")
public class CategoriaController {

    private final CategoriaRepositoryPort categoriaRepository;

    public CategoriaController(CategoriaRepositoryPort categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SOLICITANTE','ANALISTA','SUPERVISOR')")
    public ResponseEntity<?> listarActivas() {
        return ResponseEntity.ok(categoriaRepository.listarActivas());
    }
}
