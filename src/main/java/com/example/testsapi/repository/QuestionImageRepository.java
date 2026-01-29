package com.example.testsapi.repository;

import com.example.testsapi.entity.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionImageRepository extends JpaRepository<QuestionImage, Integer> {

    List<QuestionImage> findByIdQuestion(String idQuestion);
}

