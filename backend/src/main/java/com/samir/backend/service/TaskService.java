package com.samir.backend.service;

import com.samir.backend.entity.Task;
import com.samir.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Utilisé par le Chef de Projet pour voir l'état d'un projet [cite: 49]
    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    // Utilisé par le Membre pour voir ses propres tâches [cite: 51]
    public List<Task> getTasksByUser(String username) {
        return taskRepository.findByAssignedToUsername(username);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    /**
     * Logique de Time-tracking conforme au Diagramme d'Activité (Figure 4) [cite: 112]
     * Permet au membre de saisir ses heures et au système de calculer l'avancement[cite: 114, 116, 118].
     */
    public Task updateTaskHours(Long id, float consumed, float remaining) {
        Task task = getTaskById(id);

        task.setConsumedHours(consumed); // Heures consommées (H) [cite: 35, 118]
        task.setRemainingHours(remaining); // Reste à Faire (RAF) [cite: 34, 116, 118]

        // La logique d'analyse de risque PrevisionIA (> 75%) sera branchée ici en Phase 4[cite: 120, 151].

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}