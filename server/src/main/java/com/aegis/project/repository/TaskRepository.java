package com.aegis.project.repository;

import java.util.List;
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

    void deleteByParentOrgID(int orgID);

    // Custom query to update the 'completed' field for a specific task ID
    @Modifying
    @Transactional
    @Query(
            "UPDATE TaskModel t SET t.isComplete = :isComplete WHERE t.taskID = :taskID"
    )
    int updateTaskCompletedStatus(
            @Param("taskID") Integer taskID,
            @Param("isComplete") boolean isComplete
    );

    @Query("SELECT t FROM TaskModel t WHERE t.parentProjectID = :parentProjectID")
    List<TaskModel> findByParentProjectID(
            @Param("parentProjectID") int parentProjectID
    );

    @Query(
            "SELECT t FROM TaskModel t "
            + "JOIN t.assignedUsers u "
            + "WHERE t.parentProjectID = :projectID "
            + "AND (t.assignerID = :userID OR u.UserID = :userID)"
    )
    List<TaskModel> findByParentProjectIDAndUserID(
            @Param("projectID") int projectID,
            @Param("userID") int userID
    );

    @Query("SELECT t FROM TaskModel t ORDER BY t.dueDate ASC")
    List<TaskModel> findAllSorted();

    @Query(
            "SELECT t FROM TaskModel t "
            + "LEFT JOIN t.assignedUsers u "
            + "WHERE (t.assignerID = :userID OR u.id = :userID) "
            + "AND (:orgID = -1 OR t.parentOrgID = :orgID) "
            + "AND (:projectID = -1 OR t.parentProjectID = :projectID) "
            + "ORDER BY t.dueDate ASC"
    )
    Set<TaskModel> getAllUserTasks(
            @Param("userID") int userID,
            @Param("orgID") int orgID,
            @Param("projectID") int projectID
    );

    @Query(
            "SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END "
            + "FROM TaskModel t WHERE t.parentProjectID = :parentProjectID AND t.taskName = :taskName"
    )
    boolean existsTaskByProjectAndName(int parentProjectID, String taskName);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END "
            + "FROM TaskModel t JOIN t.files f "
            + "WHERE f.taskID = :taskID "
            + "AND f.fileName = :fileName "
            + "AND f.fileType = :fileType "
            + "AND f.fileData = :fileData")
    boolean existsIdenticalFile(int taskID, String fileName, String fileType, String fileData);
}
