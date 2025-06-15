package com.example.dady.repository;

import com.example.dady.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username (for authentication)
    Optional<User> findByUsername(String username);
    
    // Check if username exists
    boolean existsByUsername(String username);
} 