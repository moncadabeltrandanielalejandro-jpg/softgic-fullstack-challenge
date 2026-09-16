package com.softgic.solicitudes.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, UUID> {
    List<CategoriaEntity> findByActivaTrue();

    boolean existsByIdAndActivaTrue(UUID id);
}
