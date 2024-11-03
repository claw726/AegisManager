package com.aegis.project.repository;

import com.aegis.project.model.InvitationModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository
  extends JpaRepository<InvitationModel, Integer> {
  @Query("SELECT i FROM InvitationModel i WHERE i.RecipientID = :userID")
  List<InvitationModel> getRecipientInvitations(@Param("userID") int userID);

  @Query(
    "SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
    "FROM InvitationModel i WHERE i.Message = :message"
  )
  boolean existsInvitationByMessage(String message);
}
