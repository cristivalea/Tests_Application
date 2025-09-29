package com.example.testsapi.repository;

import com.example.testsapi.entity.QuestionLabels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionLabelsRepository extends JpaRepository<QuestionLabels, Integer> {
    List<QuestionLabels> findByIdQuestion(String idQuestion);
}
