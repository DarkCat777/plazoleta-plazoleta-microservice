package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<JpaOrderEntity, Long> {
    boolean existsByCustomerIdAndStatusIn(Long customerId, List<String> pendingStatus);
}