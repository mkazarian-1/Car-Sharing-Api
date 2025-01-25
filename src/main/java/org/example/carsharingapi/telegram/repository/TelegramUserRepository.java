package org.example.carsharingapi.telegram.repository;

import java.util.Optional;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.telegram.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TelegramUser,Long> {
    Optional<TelegramUser> findByUser(User user);

    Optional<TelegramUser> findByUserId(Long userId);
}
