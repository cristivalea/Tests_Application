package com.example.testsapi.repository;

import com.example.testsapi.entity.Dificulty;
import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Integer> {
    boolean existsByIdTest(String idTest);

}
