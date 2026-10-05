package it.hagenthon.backend.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreBandRepository extends JpaRepository<ScoreBand, Long> {
    List<ScoreBand> findAll();

    long count();
}
