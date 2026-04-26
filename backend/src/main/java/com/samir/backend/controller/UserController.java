package com.samir.backend.controller;

import com.samir.backend.entity.User;
import com.samir.backend.entity.enums.UserRole;
import com.samir.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Récupère la liste de tous les utilisateurs (Utile pour assigner des membres)
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Crée un nouvel utilisateur avec un rôle spécifique
     */
    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        // Encodage du mot de passe avant enregistrement
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Sécurité : Si aucun rôle n'est fourni, on assigne MEMBRE par défaut
        if (user.getRole() == null) {
            user.setRole(UserRole.MEMBRE);
        }

        return userRepository.save(user);
    }

    /**
     * Récupère les détails de l'utilisateur connecté
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}