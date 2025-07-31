package com.example.testsapi.repository;

import com.example.testsapi.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    List<Email> findByUser_IdUser(String idUser);

    Optional<Email> findByEmail(String email);

    Boolean existsByEmail(String email);
}