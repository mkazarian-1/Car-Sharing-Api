package org.example.carsharingapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.carsharingapi.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental,Long> {
    @EntityGraph(attributePaths = {"user", "car"})
    @Query("SELECT r FROM Rental r WHERE r.user.id = :userId "
            + "AND ((:isActive = true AND r.actualReturnDate IS NULL) "
            + "OR (:isActive = false AND r.actualReturnDate IS NOT NULL))")
    Page<Rental> findRentalsByUserIdAndActivity(@Param("userId") Long userId,
                                                @Param("isActive") boolean isActive,
                                                Pageable pageable);

    @EntityGraph(attributePaths = {"user", "car"})
    @Query("SELECT r FROM Rental r WHERE "
            + "((:isActive = true AND r.actualReturnDate IS NULL) "
            + "OR (:isActive = false AND r.actualReturnDate IS NOT NULL))")
    Page<Rental> findRentalsByActivity(@Param("isActive") boolean isActive, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "car"})
    @Query("SELECT r FROM Rental r WHERE (r.actualReturnDate IS NULL) "
            + "AND (r.returnDate < :expiryDate)")
    List<Rental> findOverdueRentals(@Param("expiryDate") LocalDate expiryDate);

    @EntityGraph(attributePaths = {"user", "car"})
    Optional<Rental> findById(Long id);

    @EntityGraph(attributePaths = {"user", "car"})
    Optional<Rental> findByIdAndUserId(Long id, Long userId);
}
