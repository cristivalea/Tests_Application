package com.example.testsapi.repository;

import com.example.testsapi.entity.TestResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface TestResultsRepository extends JpaRepository<TestResults, Integer> {

    List<TestResults> findByIdUserOrderByIdAsc(String idUser);
    long countByIdUser(String idUser);
    @Query("SELECT AVG(t.punctaj) FROM TestResults t WHERE t.idUser = :idUser")
    Double getAverageScoreByUserId(@Param("idUser") String idUser);

    @Query("SELECT COUNT(t) FROM TestResults t WHERE t.idUser = :idUser AND t.punctaj >= 65")
    long countPassedTestsByUserId(@Param("idUser") String idUser);

    @Query("SELECT AVG(t.punctaj) FROM TestResults t")
    Double getGlobalAverageScore();

    @Query("SELECT COUNT(t) FROM TestResults t WHERE t.idTest LIKE %:level%")
    long countByLevelInIdTest(@Param("level") String level);

    @Query("SELECT t.level as level, COUNT(tr) as count " +
            "FROM TestResults tr " +
            "JOIN Test t ON tr.idTest = t.idTest " +
            "GROUP BY t.level")
    List<Map<String, Object>> countTestsByDifficultyLevel();

    @Query("SELECT t.level as level, AVG(tr.punctaj) as avgScore, COUNT(tr) as count " +
            "FROM TestResults tr " +
            "JOIN Test t ON tr.idTest = t.idTest " +
            "GROUP BY t.level")
    List<Map<String, Object>> getAvgScoreByDifficultyLevel();
}