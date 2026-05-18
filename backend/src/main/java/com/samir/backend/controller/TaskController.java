// TaskController.java
package com.samir.backend.controller;

import com.samir.backend.entity.Task;
import com.samir.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @GetMapping("/my-tasks")
    public List<Task> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return taskService.getTasksByUser(userDetails.getUsername());
    }

    @PutMapping("/{id}/progress")
    public Task updateProgress(
            @PathVariable Long id,
            @RequestParam float consumed,
            @RequestParam float remaining,
            @RequestParam(required = false) String comment) { // CORRECTION ICI
        return taskService.updateTaskHours(id, consumed, remaining, comment);
    }

    @PostMapping("/metrics/federated")
    public void receiveFederatedMetrics(@RequestBody Map<String, Object> metrics) {
        taskService.saveFederatedMetrics(metrics);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Task existingTask = taskService.getTaskById(id);
        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setEstimatedHours(taskDetails.getEstimatedHours());
        existingTask.setRequiredSkill(taskDetails.getRequiredSkill());

        existingTask.setStartDate(taskDetails.getStartDate());
        existingTask.setEndDate(taskDetails.getEndDate());
        existingTask.setDependsOnTaskId(taskDetails.getDependsOnTaskId());
        existingTask.setParentTaskId(taskDetails.getParentTaskId());

        if (taskDetails.getAssignedTo() != null) {
            existingTask.setAssignedTo(taskDetails.getAssignedTo());
        }
        return taskService.createTask(existingTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}