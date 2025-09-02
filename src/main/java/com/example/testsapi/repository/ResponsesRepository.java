package com.example.testsapi.repository;


import com.example.testsapi.entity.Responses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsesRepository extends JpaRepository<Responses, Integer> {
}
