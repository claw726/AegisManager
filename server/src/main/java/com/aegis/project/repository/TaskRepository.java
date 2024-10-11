package com.aegis.project.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aegis.project.model.TaskModel;

import jakarta.transaction.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {
    void deleteByParentProjectID(int projectID);

    // Custom query to update the 'completed' field for a specific task ID
    @Modifying
    @Transactional
    @Query("UPDATE TaskModel t SET t.isComplete = :isComplete WHERE t.taskID = :taskID")
    int updateTaskCompletedStatus(@Param("taskID") Integer taskID, @Param("isComplete") boolean isComplete);

    @Query("SELECT t FROM TaskModel t " +
       "LEFT JOIN t.assignedUsers u " +
       "WHERE (t.assignerID = :userID OR u.id = :userID) " +
       "AND (:orgID = -1 OR t.parentOrgID = :orgID) " +
       "AND (:projectID = -1 OR t.parentProjectID = :projectID) " +
       "ORDER BY t.dueDate ASC")
    Set<TaskModel> getAllUserTasks(@Param("userID") int userID, @Param("orgID") int orgID, @Param("projectID") int projectID);
}
