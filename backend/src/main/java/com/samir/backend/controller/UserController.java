package com.samir.backend.controller;

import com.samir.backend.entity.User;
import com.samir.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Important pour permettre à ton app mobile de se connecter plus tard
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Récupérer tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Créer un utilisateur de test (juste pour vérifier que ça marche)
    @PostMapping("/test")
    public User createTestUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role("MEMBER")
                .build();
        return userRepository.save(user);
    }
}