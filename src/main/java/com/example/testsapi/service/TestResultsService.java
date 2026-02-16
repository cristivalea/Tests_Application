package com.example.testsapi.service;

import com.example.testsapi.entity.TestResults;
import com.example.testsapi.repository.TestResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestResultsService {

    @Autowired
    private TestResultsRepository testResultsRepository;

    public TestResultsService(TestResultsRepository testResultsRepository) {
        this.testResultsRepository = testResultsRepository;
    }

    public TestResults addTestResult(TestResults testResults){
        if(testResults == null){
            System.err.println("The result is null!");
        }
        return this.testResultsRepository.save(testResults);
    }

    public List<TestResults> findByUserIdOrderByIdAsc(String idUser) {
        return testResultsRepository.findByIdUserOrderByIdAsc(idUser);
    }
}

