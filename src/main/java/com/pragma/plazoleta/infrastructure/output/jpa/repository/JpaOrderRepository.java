package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<JpaOrderEntity, Long> {
    boolean existsByCustomerIdAndStatusIn(Long customerId, List<String> pendingStatus);

    Page<JpaOrderEntity> findAllByRestaurant_IdAndStatus(Long restaurantId, String status, Pageable pageable);
}