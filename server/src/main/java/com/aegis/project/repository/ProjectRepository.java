package com.aegis.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aegis.project.model.ProjectModel;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Integer> {
    void deleteByParentOrgID(int orgID);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM ProjectModel p WHERE p.parentOrgID = :parentOrgID AND p.projectName = :projectName")
    boolean existsProjectByOrgAndName(int parentOrgID, String projectName);

}
