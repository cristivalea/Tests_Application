package com.example.testsapi.repository;

import com.example.testsapi.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    @Query("SELECT e FROM Email e WHERE e.user.idUser = :userId")

    Email findByUserId(@Param("userId") String userId);


}