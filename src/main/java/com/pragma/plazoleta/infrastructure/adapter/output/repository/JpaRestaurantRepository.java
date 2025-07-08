package com.pragma.plazoleta.infrastructure.adapter.output.repository;

import com.pragma.plazoleta.infrastructure.adapter.output.model.JpaRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRestaurantRepository extends JpaRepository<JpaRestaurantEntity, Long> {
    boolean existsByIdAndOwnerId(Long restaurantId, Long ownerId);
}
