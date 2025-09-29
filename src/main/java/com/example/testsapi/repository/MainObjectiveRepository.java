package com.example.testsapi.repository;


import com.example.testsapi.entity.MainObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MainObjectiveRepository extends JpaRepository<MainObjective, Integer> {

    @Query(value = "SELECT IFNULL(MAX(m.id), 0) FROM main_objectives m", nativeQuery = true)
    Integer findMaxId();
    List<MainObjective> findByIdObjectiv(Integer idObjectiv);

    List<MainObjective> findByIdQuestion(String idQuestion);
}