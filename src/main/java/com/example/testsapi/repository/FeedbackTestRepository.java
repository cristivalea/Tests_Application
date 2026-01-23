package com.example.testsapi.repository;

import com.example.testsapi.entity.FeedbackTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackTestRepository extends JpaRepository<FeedbackTest, Integer> {
}
