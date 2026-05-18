package com.example.testsapi.repository;


import com.example.testsapi.entity.MainObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface MainObjectiveRepository extends JpaRepository<MainObjective, Integer> {

    @Query(value = "SELECT IFNULL(MAX(m.id), 0) FROM main_objectives m", nativeQuery = true)
    Integer findMaxId();
    List<MainObjective> findByIdObjectiv(Integer idObjectiv);

    List<MainObjective> findByIdQuestion(String idQuestion);

    long countByIdObjectiv(Integer idObjectiv);

    @Query("SELECT DISTINCT m.idObjectiv, m.nameObjectiv FROM MainObjective m ORDER BY m.idObjectiv")
    List<Object[]> findDistinctObjectives();

    @Query("SELECT m.idObjectiv as idObjectiv, m.nameObjectiv as nameObjectiv, COUNT(DISTINCT m.idQuestion) as questionCount " +
            "FROM MainObjective m " +
            "GROUP BY m.idObjectiv, m.nameObjectiv " +
            "ORDER BY m.idObjectiv")
    List<Map<String, Object>> countQuestionsByObjective();

    @Query("SELECT DISTINCT m.idQuestion FROM MainObjective m WHERE m.idObjectiv = :idObjectiv")
    List<String> findDistinctQuestionIdsByObjective(Integer idObjectiv);
}