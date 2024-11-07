package com.aegis.project.repository;

import com.aegis.project.model.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageModel, Integer> {
    List<MessageModel> findByChat_ChatIDOrderByTimestamp(int chatID);
}
