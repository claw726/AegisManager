package com.aegis.project.repository;

import com.aegis.project.model.OrgModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends JpaRepository<OrgModel, Integer> {
  /*
    @Modifying
    @Transactional
    @Query("UPDATE Org o SET o.orgName = :orgName, o.orgDescription = :orgDescription, o.orgOwnerID = :orgOwnerID WHERE o.id = :orgID")
    int updateOrg(
            @Param("orgID") int orgID,
            @Param("orgName") String orgName,
            @Param("orgDescription") String orgDescription,
            @Param("orgOwnerID") int orgOwnerID);
    */
}
