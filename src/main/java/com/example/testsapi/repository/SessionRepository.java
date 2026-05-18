package com.example.testsapi.repository;

import com.example.testsapi.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByToken(String token);
    List<Session> findByUserId(String userId);

   @Query("SELECT COUNT(DISTINCT s.userId) FROM Session s WHERE s.startSession >= :startDate")
    long countDistinctUsersByStartSessionAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT DISTINCT s.userId FROM Session s WHERE s.startSession >= :startDate")
    List<String> findDistinctUserIdsByStartSessionAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.userId = :userId AND s.startSession >= :startDate")
    long countByUserIdAndStartSessionAfter(@Param("userId") String userId,
                                           @Param("startDate") LocalDateTime startDate);
}
