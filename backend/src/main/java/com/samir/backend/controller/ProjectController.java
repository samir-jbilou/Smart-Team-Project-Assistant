package com.samir.backend.controller;

import com.samir.backend.entity.Project;
import com.samir.backend.entity.User;
import com.samir.backend.repository.ProjectRepository;
import com.samir.backend.repository.UserRepository;
import com.samir.backend.service.ProjectService; // Ajout de l'import du service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Ajout de l'import manquant
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    // On garde uniquement le Service et le UserRepository (pour l'instant)
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crée un projet via le Service
     */
    @PostMapping
    public Project createProject(@RequestBody Project project, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        project.setOwner(currentUser);

        // MODIFICATION : On utilise le service ici
        return projectService.createProject(project);
    }

    /**
     * Récupère les projets via le Service (ajoute une méthode dans le service pour ça)
     */
    @GetMapping
    public List<Project> getMyProjects(@AuthenticationPrincipal UserDetails userDetails) {
        // Idéalement, crée cette méthode dans ProjectService pour rester cohérent
        return projectService.getProjectsByOwner(userDetails.getUsername());
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<Float> getProjectProgress(@PathVariable Long id) {
        float progress = projectService.getProjectGlobalProgress(id);
        return ResponseEntity.ok(progress);
    }
}