package com.example.testsapi.repository;

import com.example.testsapi.entity.TestResolve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TestResolveRepository extends JpaRepository<TestResolve, Integer> {
    @Query("SELECT COUNT(t) FROM TestResolve t WHERE t.idQuestion = :idQuestion AND t.response = false")
    long countWrongAnswersByQuestion(@Param("idQuestion") String idQuestion);

    @Query("SELECT COUNT(t) FROM TestResolve t WHERE t.idQuestion = :idQuestion AND t.response = true")
    long countCorrectAnswersByQuestion(@Param("idQuestion") String idQuestion);

    long countByIdQuestion(String idQuestion);

    @Query(value = "SELECT idQuestion as idQuestion, COUNT(*) as wrongCount " +
            "FROM rezolvare_test " +
            "WHERE response = false " +
            "GROUP BY idQuestion " +
            "ORDER BY wrongCount DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Map<String, Object>> findTopWrongAnsweredQuestions(@Param("limit") int limit);

    @Query(value = "SELECT " +
            "idQuestion as idQuestion, " +
            "COUNT(*) as totalAnswers, " +
            "SUM(CASE WHEN response = true THEN 1 ELSE 0 END) as correctCount, " +
            "SUM(CASE WHEN response = false THEN 1 ELSE 0 END) as wrongCount, " +
            "ROUND((SUM(CASE WHEN response = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 1) as successRate " +
            "FROM rezolvare_test " +
            "WHERE idQuestion = :idQuestion " +
            "GROUP BY idQuestion",
            nativeQuery = true)
    Map<String, Object> getQuestionStatistics(@Param("idQuestion") String idQuestion);

    @Query(value = "SELECT " +
            "idQuestion as idQuestion, " +
            "COUNT(*) as totalAnswers, " +
            "SUM(CASE WHEN response = true THEN 1 ELSE 0 END) as correctCount, " +
            "SUM(CASE WHEN response = false THEN 1 ELSE 0 END) as wrongCount, " +
            "ROUND((SUM(CASE WHEN response = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 1) as successRate " +
            "FROM rezolvare_test " +
            "GROUP BY idQuestion " +
            "ORDER BY successRate ASC",
            nativeQuery = true)
    List<Map<String, Object>> getAllQuestionsWithStatistics();

    @Query(value = "SELECT " +
            "idQuestion as idQuestion, " +
            "COUNT(*) as totalAnswers, " +
            "SUM(CASE WHEN response = true THEN 1 ELSE 0 END) as correctCount, " +
            "SUM(CASE WHEN response = false THEN 1 ELSE 0 END) as wrongCount, " +
            "ROUND((SUM(CASE WHEN response = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 1) as successRate " +
            "FROM rezolvare_test " +
            "GROUP BY idQuestion " +
            "HAVING successRate < :threshold " +
            "ORDER BY successRate ASC",
            nativeQuery = true)
    List<Map<String, Object>> findProblematicQuestions(@Param("threshold") double threshold);

    @Query(value = "SELECT " +
            "idQuestion as idQuestion, " +
            "SUM(CASE WHEN response = false THEN 1 ELSE 0 END) as wrongCount " +
            "FROM rezolvare_test " +
            "GROUP BY idQuestion " +
            "HAVING wrongCount >= :minWrongAnswers " +
            "ORDER BY wrongCount DESC",
            nativeQuery = true)
    List<Map<String, Object>> findQuestionsWithMinWrongAnswers(@Param("minWrongAnswers") int minWrongAnswers);
}