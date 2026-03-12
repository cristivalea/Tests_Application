package com.example.testsapi.repository;

import com.example.testsapi.entity.TestDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDurationRepository extends JpaRepository<TestDuration, Integer> {
}
