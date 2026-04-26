package com.samir.backend.repository;

import com.samir.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Cette méthode magique de Spring Data JPA va filtrer via la relation 'owner'
    List<Project> findByOwnerUsername(String username);
}