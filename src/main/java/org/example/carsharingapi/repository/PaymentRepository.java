package org.example.carsharingapi.repository;

import org.example.carsharingapi.model.Payment;
import org.example.carsharingapi.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    @EntityGraph(attributePaths = {"rental","rental.user", "rental.car"})
    Optional<Payment> findById(Long id);

    @EntityGraph(attributePaths = {"rental","rental.user", "rental.car"})
    Optional<Payment> findBySessionId(String sessionId);

    @EntityGraph(attributePaths = {"rental","rental.user", "rental.car"})
    Page<Payment> findAllByRentalUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"rental","rental.user", "rental.car"})
    Page<Payment> findAll(Pageable pageable);
}
