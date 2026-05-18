// Task.java
package com.samir.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(name = "depends_on_task_id")
    private Long dependsOnTaskId;

    @Column(name = "parent_task_id")
    private Long parentTaskId;

    private Float estimatedHours = 0.0f;
    private Float consumedHours = 0.0f;
    private Float remainingHours = 0.0f;

    private Float failureProbability = 0.0f;
    private String iaSuggestion;

    private String status;

    @Column(name = "required_skill")
    private String requiredSkill;

    // --- NOUVEAU : Message du développeur vers le chef ---
    @Column(columnDefinition = "TEXT")
    private String developerComment;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedTo;

    @Transient
    private String blockingTaskName;

    public float getCalculatedProgress() {
        float cHours = (this.consumedHours != null) ? this.consumedHours : 0.0f;
        float rHours = (this.remainingHours != null) ? this.remainingHours : 0.0f;
        float totalWork = cHours + rHours;

        if (totalWork == 0) return 0;
        return (cHours / totalWork) * 100;
    }
}