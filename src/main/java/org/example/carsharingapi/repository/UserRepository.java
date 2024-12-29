package org.example.carsharingapi.repository;

import org.example.carsharingapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);
}
