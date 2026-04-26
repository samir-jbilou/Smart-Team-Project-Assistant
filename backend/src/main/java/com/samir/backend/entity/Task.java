package com.samir.backend.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private Float estimatedHours = 0.0f;
    private Float consumedHours = 0.0f;
    private Float remainingHours = 0.0f;

    private Float failureProbability = 0.0f;
    private String iaSuggestion;

    private String status;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedTo;

    public float getCalculatedProgress() {
        float cHours = (this.consumedHours != null) ? this.consumedHours : 0.0f;
        float rHours = (this.remainingHours != null) ? this.remainingHours : 0.0f;
        float totalWork = cHours + rHours;

        if (totalWork == 0) return 0;
        return (cHours / totalWork) * 100;
    }
}