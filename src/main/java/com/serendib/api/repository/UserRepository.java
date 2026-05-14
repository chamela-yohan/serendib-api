package com.serendib.api.repository;

import com.serendib.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Custom query: Spring read the name and builds SQL automatically:
    // SELECT * FORM users WHERE email = ?
    // Optional: might return a User, or might return empty (like null but safer)
    Optional<User> findByEmail(String email);

    // SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)
    boolean existsByEmail(String email);
}
