package com.samir.backend.controller;

import com.samir.backend.entity.Project;
import com.samir.backend.entity.User;
import com.samir.backend.repository.ProjectRepository;
import com.samir.backend.repository.UserRepository;
import com.samir.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Project createProject(@RequestBody Project project, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        project.setOwner(currentUser);
        return projectService.createProject(project);
    }

    @GetMapping
    public List<Project> getMyProjects(@AuthenticationPrincipal UserDetails userDetails) {
        return projectService.getProjectsByOwner(userDetails.getUsername());
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<Float> getProjectProgress(@PathVariable Long id) {
        float progress = projectService.getProjectGlobalProgress(id);
        return ResponseEntity.ok(progress);
    }

    // --- NOUVEAU: Modification du projet ---
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project updatedProject = projectService.updateProject(id, projectDetails);
        return ResponseEntity.ok(updatedProject);
    }

    // --- NOUVEAU: Suppression du projet ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }
}