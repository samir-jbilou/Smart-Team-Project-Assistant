package com.samir.backend.repository;

import com.samir.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Pour que le Chef de Projet voie toutes les tâches d'un projet spécifique
    List<Task> findByProjectId(Long projectId);

    // Pour que chaque Membre voie uniquement les tâches qui lui sont assignées
    List<Task> findByAssignedToUsername(String username);
}