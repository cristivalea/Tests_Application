package com.example.testsapi.repository;

import com.example.testsapi.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByToke(String token);
}
