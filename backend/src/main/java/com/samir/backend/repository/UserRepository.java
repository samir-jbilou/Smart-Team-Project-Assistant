package com.samir.backend.repository;

import com.samir.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.samir.backend.entity.enums.UserRole;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(UserRole role);
}