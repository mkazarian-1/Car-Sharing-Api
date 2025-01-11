package org.example.carsharingapi.telegram.repository;

import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.telegram.model.TelegramUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser,Long> {
    Optional<TelegramUser> findByUser(User user);

    Optional<TelegramUser> findByUserId(Long userId);

    boolean existsByUser(User user);


    @Query("""
            SELECT tu
            FROM TelegramUser tu
            JOIN tu.user u
            JOIN Rental r ON r.user = u
            JOIN Payment p ON p.rental = r
            WHERE p.sessionId = :sessionId
    """)
    Optional<TelegramUser> findByPaymentSessionId(String sessionId);
}
