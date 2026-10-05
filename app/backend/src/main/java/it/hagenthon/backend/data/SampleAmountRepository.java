package it.hagenthon.backend.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleAmountRepository extends JpaRepository<SampleAmount, Long> {
    List<SampleAmount> findAllByBand(ScoreBand band);
}
