package com.example.testsapi.repository;


import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, QuestionId> {
}
