package com.aegis.project.repository;

import com.aegis.project.model.ProjectModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository
  extends JpaRepository<ProjectModel, Integer> {
  void deleteByParentOrgID(int orgID);

  @Query("SELECT p FROM ProjectModel p WHERE p.parentOrgID = :parentOrgID")
  List<ProjectModel> findByParentOrgID(@Param("parentOrgID") int parentOrgID);

  @Query(
    "SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
    "FROM ProjectModel p WHERE p.parentOrgID = :parentOrgID AND p.projectName = :projectName"
  )
  boolean existsProjectByOrgAndName(int parentOrgID, String projectName);

  @Query(
    "SELECT p FROM ProjectModel p JOIN p.assignedUsers u WHERE p.projectOwnerID = :userID OR u.UserID = :userID"
  )
  List<ProjectModel> findAllUserProjects(@Param("userID") int userID);
}
