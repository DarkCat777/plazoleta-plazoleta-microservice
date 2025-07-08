package com.pragma.plazoleta.infrastructure.adapter.output.repository;

import com.pragma.plazoleta.infrastructure.adapter.output.model.JpaDishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDishRepository extends JpaRepository<JpaDishEntity, Long> {
}
