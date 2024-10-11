package com.aegis.project.repository;

import com.aegis.project.model.ProjectModel;
import com.aegis.project.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aegis.project.model.TaskModel;

import jakarta.transaction.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {
    void deleteByParentProjectID(int projectID);
}
