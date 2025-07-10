package com.pragma.plazoleta.dish.infrastructure.adapter.output.repository;

import com.pragma.plazoleta.dish.infrastructure.adapter.output.model.JpaCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCategoryRepository extends JpaRepository<JpaCategoryEntity, Long> {
}
