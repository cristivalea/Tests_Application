package com.example.testsapi.repository;

import com.example.testsapi.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<Response, Integer> {
}
