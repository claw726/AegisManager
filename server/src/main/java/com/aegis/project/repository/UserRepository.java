package com.aegis.project.repository;

import com.aegis.project.model.UserModel;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByUserName(String userName);

    @Modifying
    @Transactional
    @Query("update UserModel u set u.failedLoginAttempts = ?1, u.isLocked = ?2 where u.UserID = ?3")
    void updateFailedLoginAttempts(int failedLoginAttempts, boolean isLocked, int UserID);
}
