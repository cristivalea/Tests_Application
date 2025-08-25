package com.example.testsapi.repository;

import com.example.testsapi.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository  extends JpaRepository<Question, Integer> {
}
