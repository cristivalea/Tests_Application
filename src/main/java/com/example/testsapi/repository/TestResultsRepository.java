package com.example.testsapi.repository;

import com.example.testsapi.entity.TestResults;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultsRepository extends JpaRepository<TestResults, CriteriaBuilder.In> {
}
