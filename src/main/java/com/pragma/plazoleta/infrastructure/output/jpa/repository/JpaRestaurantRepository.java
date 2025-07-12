package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRestaurantRepository extends JpaRepository<JpaRestaurantEntity, Long> {
    boolean existsByIdAndOwnerId(Long restaurantId, Long ownerId);
}
