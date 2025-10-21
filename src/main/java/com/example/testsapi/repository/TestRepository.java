package com.example.testsapi.repository;

import com.example.testsapi.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Integer> {
    boolean existsByIdTest(String idTest);
}
