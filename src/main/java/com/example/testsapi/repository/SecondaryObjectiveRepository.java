package com.example.testsapi.repository;


import com.example.testsapi.entity.SecondaryObjective;
import com.example.testsapi.entity.SecondaryObjectiveId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondaryObjectiveRepository extends JpaRepository<SecondaryObjective, SecondaryObjectiveId> {

    @Query(value = "SELECT IFNULL(MAX(s.id), 0) FROM secondary_objectives s", nativeQuery = true)
    Integer findMaxId();
    List<SecondaryObjective> findByIdObjectiv(Integer idObjectiv);
}