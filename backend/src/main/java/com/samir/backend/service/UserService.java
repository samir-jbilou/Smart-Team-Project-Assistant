package com.samir.backend.service;

import com.samir.backend.entity.User;
import com.samir.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // AJOUT
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // AJOUT

    public User createUser(User user) {
        // 1. Hachage du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 2. Rôle par défaut si non spécifié
        if (user.getRole() == null) {
            user.setRole(com.samir.backend.entity.enums.UserRole.MEMBRE);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}