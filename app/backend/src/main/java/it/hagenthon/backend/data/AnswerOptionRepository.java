package it.hagenthon.backend.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    List<AnswerOption> findAllByQuestionOrderByOrdineAsc(Question question);

    List<AnswerOption> findAllByQuestion(Question question);
}
