package com.aegis.project.repository;

import java.util.Set;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.TaskModel;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {
    void deleteByParentProjectID(int projectID);

    // Custom query to update the 'completed' field for a specific task ID
    @Modifying
    @Transactional
    @Query("UPDATE TaskModel t SET t.completed = :completed WHERE t.id = :id")
    int updateTaskCompletedStatus(@Param("id") Integer id, @Param("completed") boolean completed);


    @Query("SELECT t FROM tasks t " +
           "LEFT JOIN t.assignedUsers u " +
           "WHERE (t.assignerID = :userID OR u.id = :userID) " +
           "AND (:orgID = -1 OR t.parentOrgID = :orgID) " +
           "AND (:projectID = -1 OR t.parentProjectID = :projectID) " +
           "ORDER BY t.dueDate ASC")
    Set<TaskModel> getAllUserTasks(@Param("userID") int userID, @Param("orgID") int orgID, @Param("projectID") int projectID);
}
