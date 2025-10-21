package com.example.testsapi.repository;

import com.example.testsapi.entity.QuestionTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionTestRepository extends JpaRepository<QuestionTest, Integer> {
    List<QuestionTest> findByIdTest(String idTest);
}
