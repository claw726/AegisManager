package com.aegis.project.repository;

import com.aegis.project.model.ChatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatModel, Integer> {
    List<ChatModel> findByParticipantsContaining(int userID);
}
