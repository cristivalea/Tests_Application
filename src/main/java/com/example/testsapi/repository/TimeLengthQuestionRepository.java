package com.example.testsapi.repository;

import com.example.testsapi.entity.TimeLengthQuestion;
import com.example.testsapi.entity.TimeLengthQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeLengthQuestionRepository extends JpaRepository<TimeLengthQuestion, TimeLengthQuestionId> {
    List<TimeLengthQuestion> findByTimeLengthBetween(Integer minTime, Integer maxTime);
    List<TimeLengthQuestion> findByIdQuestion(String idQuestion);

    @Query(value = "SELECT IFNULL(MAX(m.id), 0) FROM time_length_question m", nativeQuery = true)
    Integer findMaxId();
    List<TimeLengthQuestion> findByTimeLength(Integer timeLength);
}