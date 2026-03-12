package com.example.testsapi.service;

import com.example.testsapi.entity.TestDuration;
import com.example.testsapi.repository.TestDurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestDurationService {

    @Autowired
    private TestDurationRepository testDurationRepository;

    public TestDurationService(TestDurationRepository testDurationRepository) {
        this.testDurationRepository = testDurationRepository;
    }

    public TestDuration addTestDuration(TestDuration testDuration){
        return this.testDurationRepository.save(testDuration);
    }
}
