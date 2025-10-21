package com.example.testsapi.repository;

import com.example.testsapi.entity.UserTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTestRepository extends JpaRepository<UserTest, Integer> {
}
