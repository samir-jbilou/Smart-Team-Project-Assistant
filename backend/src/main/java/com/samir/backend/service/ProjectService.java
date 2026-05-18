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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService; // Import vital pour la synchronisation

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    // --- Mise à jour des informations du projet ---
    public Project updateProject(Long id, Project projectDetails) {
        Project existingProject = getProjectById(id);

        existingProject.setName(projectDetails.getName());
        existingProject.setDescription(projectDetails.getDescription());
        existingProject.setMethodology(projectDetails.getMethodology());
        existingProject.setStartDate(projectDetails.getStartDate());
        existingProject.setEndDate(projectDetails.getEndDate());
        existingProject.setStatus(projectDetails.getStatus());

        return projectRepository.save(existingProject);
    }

    public float getProjectGlobalProgress(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        if (tasks.isEmpty()) return 0;

        float totalProgress = 0;
        for (Task task : tasks) {
            totalProgress += task.getCalculatedProgress();
        }
        return totalProgress / tasks.size();
    }

    public Project addMemberToProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!project.getMembers().contains(user)) {
            project.getMembers().add(user);
        }

        return projectRepository.save(project);
    }

    // --- CORRECTION: Synchronisation lors de la suppression ---
    public void deleteProject(Long id) {
        Project project = getProjectById(id);

        // 1. Lister tous les membres qui ont des tâches dans ce projet
        Set<Long> userIdsToUpdate = project.getTasks().stream()
                .filter(t -> t.getAssignedTo() != null)
                .map(t -> t.getAssignedTo().getId())
                .collect(Collectors.toSet());

        // 2. Supprimer le projet (et ses tâches en cascade)
        projectRepository.deleteById(id);
        projectRepository.flush(); // Force la suppression immédiate en BDD

        // 3. Recalculer la charge de travail des membres impactés
        for (Long userId : userIdsToUpdate) {
            userService.recalculateWorkload(userId);
        }
    }

    public List<Project> getProjectsByOwner(String username) {
        return projectRepository.findByOwnerUsername(username);
    }
}