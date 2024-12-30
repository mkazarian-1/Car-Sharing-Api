package org.example.carsharingapi.repository;

import java.util.Optional;
import org.example.carsharingapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    boolean existsByEmail(String email);
}
