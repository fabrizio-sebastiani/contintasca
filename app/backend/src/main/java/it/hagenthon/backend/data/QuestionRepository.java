package it.hagenthon.backend.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByOrderByOrdineAsc();

    Optional<Question> findByCodice(String codice);

    long count();
}
