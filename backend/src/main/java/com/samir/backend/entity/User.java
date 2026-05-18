package com.samir.backend.entity;

import com.samir.backend.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role; // Utilise maintenant l'Enum conforme au CDC

    // AI Edge Analysis Metrics

    @Column(name = "seniority_level")
    private Integer seniorityLevel = 1; // Default to Junior

    @Column(name = "primary_skill")
    private String primarySkill = "GENERAL";

    @Column(name = "efficiency_score")
    private Float efficiencyScore = 1.0f; // Default baseline (100% efficient)

    @Column(name = "tasks_completed_on_time")
    private Integer tasksCompletedOnTime = 0;

    @Column(name = "current_workload_hours")
    private Float currentWorkloadHours = 0.0f;

}
