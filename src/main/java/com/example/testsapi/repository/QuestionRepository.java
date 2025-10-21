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

}
