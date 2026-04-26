package com.samir.backend.entity;

import com.samir.backend.entity.enums.ProjectMethodology; // Import de l'Enum
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    // --- AJOUT CONFORME AU CAHIER DES CHARGES ---
    @Enumerated(EnumType.STRING)
    private ProjectMethodology methodology; // Correspond à MethodEnum [cite: 106]
    // --------------------------------------------

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default // Indique à Lombok de garder l'initialisation par défaut
    private List<User> members = new java.util.ArrayList<>();

    // Tes getters/setters manuels (au cas où Lombok tarde à compiler)
    public void setOwner(User owner) { this.owner = owner; }
    public User getOwner() { return owner; }
}