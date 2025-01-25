package org.example.carsharingapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findAllByRoles(Set<UserRole> roles);
}
