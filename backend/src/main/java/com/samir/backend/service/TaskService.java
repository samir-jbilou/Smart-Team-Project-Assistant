// TaskService.java
package com.samir.backend.service;

import com.samir.backend.entity.Project;
import com.samir.backend.entity.Task;
import com.samir.backend.entity.User;
import com.samir.backend.repository.ProjectRepository;
import com.samir.backend.repository.TaskRepository;
import com.samir.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    public List<Task> getAllTasks() {
        return populateTransientFields(taskRepository.findAll());
    }

    public List<Task> getTasksByProject(Long projectId) {
        return populateTransientFields(taskRepository.findByProjectId(projectId));
    }

    public List<Task> getTasksByUser(String username) {
        return populateTransientFields(taskRepository.findByAssignedToUsername(username));
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    private List<Task> populateTransientFields(List<Task> tasks) {
        for (Task task : tasks) {
            if (task.getDependsOnTaskId() != null) {
                taskRepository.findById(task.getDependsOnTaskId()).ifPresent(depTask -> {
                    if (!"TERMINE".equals(depTask.getStatus())) {
                        task.setBlockingTaskName(depTask.getTitle());
                    }
                });
            }
        }
        return tasks;
    }

    public Task createTask(Task task) {
        if (task.getProject() != null && task.getProject().getId() != null) {
            task.setProject(projectRepository.findById(task.getProject().getId()).orElseThrow());
        }
        if (task.getAssignedTo() != null && task.getAssignedTo().getId() != null) {
            task.setAssignedTo(userRepository.findById(task.getAssignedTo().getId()).orElseThrow());
        }

        task.setStatus("A_FAIRE");
        Task savedTask = taskRepository.save(task);

        if (savedTask.getParentTaskId() != null) recalculateParentTask(savedTask.getParentTaskId());
        updateUserWorkload(savedTask);
        return savedTask;
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = getTaskById(id);
        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setEstimatedHours(taskDetails.getEstimatedHours());
        existingTask.setRequiredSkill(taskDetails.getRequiredSkill());

        existingTask.setStartDate(taskDetails.getStartDate());
        existingTask.setEndDate(taskDetails.getEndDate());
        existingTask.setDependsOnTaskId(taskDetails.getDependsOnTaskId());

        Long oldParentId = existingTask.getParentTaskId();
        existingTask.setParentTaskId(taskDetails.getParentTaskId());

        Long oldUserId = existingTask.getAssignedTo() != null ? existingTask.getAssignedTo().getId() : null;
        if (taskDetails.getAssignedTo() != null) existingTask.setAssignedTo(taskDetails.getAssignedTo());

        Task savedTask = taskRepository.save(existingTask);

        if (oldParentId != null) recalculateParentTask(oldParentId);
        if (savedTask.getParentTaskId() != null && !savedTask.getParentTaskId().equals(oldParentId)) {
            recalculateParentTask(savedTask.getParentTaskId());
        }

        if (oldUserId != null) userService.recalculateWorkload(oldUserId);
        updateUserWorkload(savedTask);

        return savedTask;
    }

    // CORRECTION : Prise en charge du paramètre Comment + Fix du NullPointerException
    public Task updateTaskHours(Long id, float consumed, float remaining, String comment) {
        Task task = getTaskById(id);

        float currentConsumed = task.getConsumedHours() != null ? task.getConsumedHours() : 0f;

        if (task.getDependsOnTaskId() != null && consumed > currentConsumed) {
            Task depTask = taskRepository.findById(task.getDependsOnTaskId()).orElse(null);
            if (depTask != null && !"TERMINE".equals(depTask.getStatus())) {
                throw new RuntimeException("DEPENDENCY_BLOCKED:" + depTask.getTitle());
            }
        }

        task.setConsumedHours(consumed);
        task.setRemainingHours(remaining);
        task.setStatus(remaining <= 0 ? "TERMINE" : "EN COURS");

        if (comment != null) {
            task.setDeveloperComment(comment);
        }

        Task savedTask = taskRepository.save(task);
        if (savedTask.getParentTaskId() != null) recalculateParentTask(savedTask.getParentTaskId());
        updateUserWorkload(savedTask);
        return savedTask;
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        Long userId = task.getAssignedTo() != null ? task.getAssignedTo().getId() : null;
        Long parentId = task.getParentTaskId();

        List<Task> dependents = taskRepository.findByDependsOnTaskId(id);
        for(Task dep : dependents) {
            dep.setDependsOnTaskId(null);
            taskRepository.save(dep);
        }

        List<Task> children = taskRepository.findByParentTaskId(id);
        for(Task child : children) {
            deleteTask(child.getId());
        }

        taskRepository.deleteById(id);
        taskRepository.flush();

        if (parentId != null) recalculateParentTask(parentId);
        if (userId != null) userService.recalculateWorkload(userId);
    }

    private void recalculateParentTask(Long parentId) {
        Task parent = taskRepository.findById(parentId).orElse(null);
        if (parent == null) return;

        List<Task> children = taskRepository.findByParentTaskId(parentId);
        if (children.isEmpty()) return;

        LocalDate minStart = children.stream().map(Task::getStartDate).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(parent.getStartDate());
        LocalDate maxEnd = children.stream().map(Task::getEndDate).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(parent.getEndDate());

        parent.setStartDate(minStart);
        parent.setEndDate(maxEnd);

        float totalEstimated = 0f, totalConsumed = 0f, totalRemaining = 0f;
        for (Task child : children) {
            totalEstimated += (child.getEstimatedHours() != null ? child.getEstimatedHours() : 0);
            totalConsumed += (child.getConsumedHours() != null ? child.getConsumedHours() : 0);
            totalRemaining += (child.getRemainingHours() != null ? child.getRemainingHours() : (child.getEstimatedHours() != null ? child.getEstimatedHours() : 0));
        }

        parent.setEstimatedHours(totalEstimated);
        parent.setConsumedHours(totalConsumed);
        parent.setRemainingHours(totalRemaining);

        taskRepository.save(parent);
    }

    private void updateUserWorkload(Task task) {
        if (task.getAssignedTo() != null && task.getAssignedTo().getId() != null) {
            userService.recalculateWorkload(task.getAssignedTo().getId());
        }
    }

    public void saveFederatedMetrics(Map<String, Object> metrics) {
        try (FileWriter fw = new FileWriter("training_data.csv", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.printf(Locale.US, "%.1f,%.1f,%d,%.2f,%.1f,%.1f,%.1f,%.2f,%d\n",
                    ((Number) metrics.get("eac_hours")).floatValue(),
                    ((Number) metrics.get("estimated_hours")).floatValue(),
                    ((Number) metrics.get("seniority")).intValue(),
                    ((Number) metrics.get("efficiency")).floatValue(),
                    ((Number) metrics.get("workload")).floatValue(),
                    ((Number) metrics.get("skill_match")).floatValue(),
                    ((Number) metrics.get("methodology")).floatValue(),
                    ((Number) metrics.get("time_pressure")).floatValue(),
                    ((Number) metrics.get("is_delayed")).intValue());
        } catch (Exception ignored) {}
    }
}