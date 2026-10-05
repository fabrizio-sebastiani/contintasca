package it.hagenthon.backend.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRangeRepository extends JpaRepository<AnswerRange, Long> {
    List<AnswerRange> findAllByQuestion(Question question);
}
