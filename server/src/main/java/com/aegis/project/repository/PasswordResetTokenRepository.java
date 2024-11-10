package com.aegis.project.repository;

import com.aegis.project.model.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository
  extends JpaRepository<PasswordResetToken, Integer> {
  Optional<PasswordResetToken> findByToken(String token);
  void deleteByToken(String token);
}
