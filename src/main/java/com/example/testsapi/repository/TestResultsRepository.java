package com.example.testsapi.repository;

import com.example.testsapi.entity.TestResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestResultsRepository extends JpaRepository<TestResults, Integer> {

    // Corectat: IdUser (cu I mare) corespunde variabilei idUser din Entity
    List<TestResults> findByIdUserOrderByIdAsc(String idUser);
}