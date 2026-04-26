package com.samir.backend.controller;

import com.samir.backend.entity.Task;
import com.samir.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Pour le Chef de Projet : Suivre l'avancement d'un projet spécifique (Fig. 1)
     */
    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    /**
     * Pour le Membre : Consulter ses propres tâches via son Token JWT (Fig. 1)
     */
    @GetMapping("/my-tasks")
    public List<Task> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return taskService.getTasksByUser(userDetails.getUsername());
    }

    /**
     * Time-tracking : Saisie des heures consommées (H) et du RAF (Fig. 2 et 4)
     */
    @PutMapping("/{id}/progress")
    public Task updateProgress(
            @PathVariable Long id,
            @RequestParam float consumed,
            @RequestParam float remaining) {
        return taskService.updateTaskHours(id, consumed, remaining);
    }

    /**
     * Synchronisation PrevisionIA : Enregistre les prédictions calculées par le smartphone.
     * Permet au Chef de Projet de voir les alertes (Proba > 75%) et suggestions d'IA.
     */
    @PutMapping("/{id}/ai-update")
    public Task updateAIResults(
            @PathVariable Long id,
            @RequestParam float probability,
            @RequestParam String suggestion) {
        Task task = taskService.getTaskById(id);
        task.setFailureProbability(probability);
        task.setIaSuggestion(suggestion);
        return taskService.createTask(task); // Enregistre les nouveaux champs IA
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}