package com.example.testsapi.repository;

import com.example.testsapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    @Query("SELECT MAX(CAST(SUBSTRING(u.idUser, 2) AS integer)) FROM User u WHERE u.idUser LIKE 'U%'")
    Integer findMaxUserIdNumber();
}