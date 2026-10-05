package it.hagenthon.backend.data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.hagenthon.backend.common.ApiException;
import it.hagenthon.backend.common.Timestamps;

/**
 * RB-20, RB-21, RB-22: controlli sul file intero, sostituzione transazionale dei dati.
 */
@Service
public class DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);
    private static final long LIMITE_BYTE = 2L * 1024 * 1024;

    private final CsvParser csvParser;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final AnswerRangeRepository answerRangeRepository;
    private final ScoreBandRepository scoreBandRepository;
    private final SampleAmountRepository sampleAmountRepository;
    private final DataLoadRepository dataLoadRepository;

    public DataImportService(CsvParser csvParser, QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository, AnswerRangeRepository answerRangeRepository,
            ScoreBandRepository scoreBandRepository, SampleAmountRepository sampleAmountRepository,
            DataLoadRepository dataLoadRepository) {
        this.csvParser = csvParser;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.answerRangeRepository = answerRangeRepository;
        this.scoreBandRepository = scoreBandRepository;
        this.sampleAmountRepository = sampleAmountRepository;
        this.dataLoadRepository = dataLoadRepository;
    }

    public DatiAttualiResponse datiAttuali() {
        return dataLoadRepository.findAll().stream().findFirst()
                .map(d -> new DatiAttualiResponse(true, d.getNomeFile(), Timestamps.format(d.getCaricatoIl()),
                        d.getNumeroDomande(), d.getNumeroFasce()))
                .orElse(new DatiAttualiResponse(false, null, null, 0, 0));
    }

    @Transactional
    public CaricamentoResponse importFile(String nomeFileOriginale, byte[] bytes) {
        try {
            return eseguiImport(nomeFileOriginale, bytes);
        } catch (ApiException e) {
            log.info("Import rifiutato: codice={}", e.getCodice());
            throw e;
        }
    }

    private CaricamentoResponse eseguiImport(String nomeFileOriginale, byte[] bytes) {
        if (nomeFileOriginale == null || nomeFileOriginale.isBlank()) {
            throw new ApiException(400, "FILE_MANCANTE", "Nessun file");
        }
        if (bytes == null || bytes.length > LIMITE_BYTE) {
            throw new ApiException(413, "FILE_TROPPO_GRANDE", "File troppo grande");
        }
        if (!nomeFileOriginale.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new ApiException(400, "FILE_NON_CSV", "File non CSV");
        }

        // Si legge e valida tutto in memoria prima di toccare il database (RB-22).
        CsvParseResult parsed = csvParser.parse(bytes);

        sampleAmountRepository.deleteAllInBatch();
        answerOptionRepository.deleteAllInBatch();
        answerRangeRepository.deleteAllInBatch();
        scoreBandRepository.deleteAllInBatch();
        questionRepository.deleteAllInBatch();
        dataLoadRepository.deleteAllInBatch();

        Map<String, Question> questionByCodice = new LinkedHashMap<>();
        int ordine = 0;
        for (ParsedQuestion pq : parsed.domande) {
            Question q = new Question();
            q.setCodice(pq.codice);
            q.setTipo(pq.tipo);
            q.setEtichetta(pq.etichetta);
            q.setTesto(pq.testo);
            q.setSpiegazione(pq.spiegazione);
            q.setOrdine(ordine++);
            q = questionRepository.save(q);
            questionByCodice.put(pq.codice, q);

            for (ParsedOption po : pq.opzioni) {
                AnswerOption opt = new AnswerOption();
                opt.setQuestion(q);
                opt.setCodice(po.codice);
                opt.setTesto(po.testo);
                opt.setPunteggio(po.punteggio);
                opt.setOrdine(po.ordine);
                answerOptionRepository.save(opt);
            }
            for (ParsedRange pr : pq.fasceRisposta) {
                AnswerRange range = new AnswerRange();
                range.setQuestion(q);
                range.setMinimo(pr.minimo);
                range.setMassimo(pr.massimo);
                range.setPunteggio(pr.punteggio);
                answerRangeRepository.save(range);
            }
        }

        for (ParsedScoreBand pb : parsed.fasce) {
            ScoreBand band = new ScoreBand();
            band.setCodice(pb.codice);
            band.setMinimo(pb.minimo);
            band.setMassimo(pb.massimo);
            band = scoreBandRepository.save(band);
            for (Map.Entry<Voce, BigDecimal> e : pb.importi.entrySet()) {
                SampleAmount amount = new SampleAmount();
                amount.setBand(band);
                amount.setVoce(e.getKey());
                amount.setImporto(e.getValue());
                sampleAmountRepository.save(amount);
            }
        }

        DataLoad load = new DataLoad();
        load.setNomeFile(nomeFileOriginale);
        load.setCaricatoIl(Timestamps.now());
        load.setNumeroDomande(parsed.domande.size());
        load.setNumeroFasce(parsed.fasce.size());
        load = dataLoadRepository.save(load);

        log.info("Import completato: lette={} importate={} scartate={}", parsed.righeLette, parsed.righeImportate,
                parsed.righeScartate);

        DatiAttualiResponse datiAttuali = new DatiAttualiResponse(true, load.getNomeFile(),
                Timestamps.format(load.getCaricatoIl()), load.getNumeroDomande(), load.getNumeroFasce());
        return new CaricamentoResponse(parsed.righeLette, parsed.righeImportate, parsed.righeScartate, parsed.scarti,
                datiAttuali);
    }
}
