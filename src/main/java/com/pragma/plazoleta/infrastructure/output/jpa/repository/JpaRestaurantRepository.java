package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaRestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRestaurantRepository extends JpaRepository<JpaRestaurantEntity, Long> {
    boolean existsByIdAndOwnerId(Long restaurantId, Long ownerId);

    Optional<JpaRestaurantEntity> findByOwnerId(Long ownerId);
}
