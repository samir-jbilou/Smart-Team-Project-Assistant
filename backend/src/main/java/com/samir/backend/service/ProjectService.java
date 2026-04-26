package com.samir.backend.service;

import com.samir.backend.entity.Project;
import com.samir.backend.entity.Task;
import com.samir.backend.entity.User;
import com.samir.backend.repository.ProjectRepository;
import com.samir.backend.repository.TaskRepository; // Ajout nécessaire
import com.samir.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository; // Injecté pour accéder aux tâches du projet

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    /**
     * Calcule l'avancement global du projet (Conforme à la Figure 3 du CDC)
     * Calcule la moyenne de l'avancement de toutes les tâches associées.
     */
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

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> getProjectsByOwner(String username) {
        return projectRepository.findByOwnerUsername(username);
    }
}