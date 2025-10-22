package com.example.testsapi.repository;


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



}
