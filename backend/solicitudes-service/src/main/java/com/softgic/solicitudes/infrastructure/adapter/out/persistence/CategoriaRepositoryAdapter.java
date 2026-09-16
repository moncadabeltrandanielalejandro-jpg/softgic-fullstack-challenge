package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import com.softgic.solicitudes.domain.port.out.CategoriaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository jpaRepository;

    public CategoriaRepositoryAdapter(CategoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<CategoriaDto> listarActivas() {
        return jpaRepository.findByActivaTrue().stream()
                .map(c -> new CategoriaDto(c.getId(), c.getNombre(), c.isActiva()))
                .toList();
    }

    @Override
    public boolean existeActiva(UUID categoriaId) {
        return jpaRepository.existsByIdAndActivaTrue(categoriaId);
    }
}
