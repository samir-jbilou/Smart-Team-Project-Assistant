package com.samir.backend.repository;

import com.samir.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Cette méthode magique permettra de chercher un utilisateur par son email
    Optional<User> findByEmail(String email);
}