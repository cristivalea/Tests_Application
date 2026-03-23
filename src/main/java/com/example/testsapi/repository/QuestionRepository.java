package com.example.testsapi.repository;


import com.example.testsapi.entity.Dificulty;
import com.example.testsapi.entity.Question;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.entity.TimeLengthQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, QuestionId> {
    @Query(value = "SELECT * FROM question q WHERE JSON_UNQUOTE(JSON_EXTRACT(q.question, '$.mainObjectives[0].name')) = :objective", nativeQuery = true)
    List<Question> findByMainObjective(@Param("objective") String objective);
    Optional<Question> findByQuestionId_IdQuestion(String idQuestion);
    List<Question> findAllByQuestionId_IdQuestionIn(List<String> ids);

    //q.level = :level → filtrează întrebările după nivel (SOFTQUESTION, MIDLELEVELQUESTION, etc.).
    //
    //JSON_EXTRACT(q.question, '$.mainObjectives[0].id') → extrage primul obiectiv principal din JSON.
    //
    //JSON_EXTRACT(q.question, '$.secondaryObjectives[0].id') → extrage primul obiectiv secundar.
    //
    //JSON_UNQUOTE(...) → elimină ghilimelele din rezultatul JSON.
    @Query(value = """
    SELECT * FROM question q
    WHERE q.level = :level
      AND JSON_UNQUOTE(JSON_EXTRACT(q.question, '$.mainObjectives[0].id')) = :mainObjectiveId
      AND JSON_UNQUOTE(JSON_EXTRACT(q.question, '$.secondaryObjectives[0].id')) = :secondaryObjectiveId
    """, nativeQuery = true)
    List<Question> findByLevelAndObjectives(
            @Param("level") String level,
            @Param("mainObjectiveId") int mainObjectiveId,
            @Param("secondaryObjectiveId") int secondaryObjectiveId);
//    @Query(value = """
//SELECT * FROM question q
//WHERE q.level = :level
//  AND JSON_UNQUOTE(JSON_EXTRACT(q.question, '$.mainObjectives[0].id')) = :mainObjectiveId
//  AND JSON_UNQUOTE(JSON_EXTRACT(q.question, '$.secondaryObjectives[0].id')) = :secondaryObjectiveId
//""", nativeQuery = true)
//    List<Question> findByLevelAndObjectives(
//            @Param("level") String level,
//            @Param("mainObjectiveId") String mainObjectiveId,   // STRING!
//            @Param("secondaryObjectiveId") String secondaryObjectiveId); // STRING!
@Query("""
SELECT q FROM Question q
JOIN SecondaryObjective so ON so.idQuestion = q.questionId.idQuestion
WHERE so.idObjectiv = :subId
AND q.level = :level
""")
List<Question> findBySecondaryObjectiveAndLevel(
        @Param("subId") int subId,
        @Param("level") String level
);

    boolean existsByQuestionId_IdQuestion(String idQuestion);
    @Query("""
    SELECT COUNT(q) 
    FROM Question q 
    JOIN QuestionTest qt ON q.questionId.idQuestion = qt.idQuestion 
    WHERE qt.idTest = :testId AND q.score = 1
""")
    int countGradedQuestionsByTestId(@Param("testId") String testId);
}
