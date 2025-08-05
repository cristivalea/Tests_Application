package com.example.testsapi.repository;

import com.example.testsapi.entity.Email;
import com.example.testsapi.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    @Query("SELECT q FROM SecurityQuestion q WHERE q.user.idUser = :userId")
    List<SecurityQuestion> getAllByUserId(@Param("userId") String userId);

}
