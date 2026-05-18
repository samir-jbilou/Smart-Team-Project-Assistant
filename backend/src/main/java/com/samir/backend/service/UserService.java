package com.samir.backend.service;

import com.samir.backend.entity.User;
import com.samir.backend.repository.UserRepository;
import com.samir.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.samir.backend.entity.enums.UserRole;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getMembers() {
        return userRepository.findByRole(UserRole.MEMBRE);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(UserRole.MEMBRE);
        }

        return userRepository.save(user);
    }

    // --- NOUVEAU : Mécanisme d'Auto-Guérison (Self-Healing) ---
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        for(User u : users) {
            // On recalcule la charge réelle actuelle depuis les tâches existantes
            Float actualWorkload = taskRepository.sumActiveWorkloadByUserId(u.getId());
            if (actualWorkload == null) actualWorkload = 0f;

            // Si la base de données est désynchronisée, on la répare silencieusement
            if (u.getCurrentWorkloadHours() == null || Math.abs(u.getCurrentWorkloadHours() - actualWorkload) > 0.01) {
                u.setCurrentWorkloadHours(actualWorkload);
                userRepository.save(u);
            }
        }
        return users;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserCharacteristics(Long id, Integer seniority, Float efficiency, String primarySkill) {
        User user = getUserById(id);
        if (seniority != null) user.setSeniorityLevel(seniority);
        if (efficiency != null) user.setEfficiencyScore(efficiency);
        if (primarySkill != null) user.setPrimarySkill(primarySkill);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void recalculateWorkload(Long userId) {
        Float newWorkload = taskRepository.sumActiveWorkloadByUserId(userId);
        if (newWorkload == null) newWorkload = 0f; // Protection contre les valeurs nulles

        User user = getUserById(userId);
        user.setCurrentWorkloadHours(newWorkload);
        userRepository.save(user);
    }
}