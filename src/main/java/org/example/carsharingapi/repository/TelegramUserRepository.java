package org.example.carsharingapi.repository;

import java.util.Optional;
import org.example.carsharingapi.model.TelegramUser;
import org.example.carsharingapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TelegramUser,Long> {
    Optional<TelegramUser> findByUser(User user);

    Optional<TelegramUser> findByUserId(Long userId);
}
