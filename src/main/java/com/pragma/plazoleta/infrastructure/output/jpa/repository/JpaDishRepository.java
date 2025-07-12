package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaDishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDishRepository extends JpaRepository<JpaDishEntity, Long> {
    Page<JpaDishEntity> findAllByRestaurant_IdAndCategory_Id(Long restaurantId, Long categoryId, Pageable pageable);
}
