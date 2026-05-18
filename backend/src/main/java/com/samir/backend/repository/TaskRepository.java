// TaskRepository.java
package com.samir.backend.repository;

import com.samir.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignedToUsername(String username);

    List<Task> findByParentTaskId(Long parentTaskId);

    // NOUVEAU : Pour trouver rapidement les tâches dépendantes lors d'une suppression
    List<Task> findByDependsOnTaskId(Long dependsOnTaskId);

    // CORRECTION : On ne somme la charge QUE si la tâche n'est pas bloquée par une autre tâche non-terminée
    @Query("SELECT COALESCE(SUM(COALESCE(t.remainingHours, t.estimatedHours)), 0f) FROM Task t " +
            "WHERE t.assignedTo.id = :userId AND t.status != 'TERMINE' " +
            "AND (t.dependsOnTaskId IS NULL OR EXISTS (SELECT 1 FROM Task d WHERE d.id = t.dependsOnTaskId AND d.status = 'TERMINE'))")
    Float sumActiveWorkloadByUserId(@Param("userId") Long userId);
}