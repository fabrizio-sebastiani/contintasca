package it.hagenthon.backend.profile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.hagenthon.backend.common.ApiException;
import it.hagenthon.backend.common.ErrorDetail;
import it.hagenthon.backend.common.Timestamps;
import it.hagenthon.backend.data.AnswerOption;
import it.hagenthon.backend.data.AnswerOptionRepository;
import it.hagenthon.backend.data.AnswerRange;
import it.hagenthon.backend.data.AnswerRangeRepository;
import it.hagenthon.backend.data.DataLoadRepository;
import it.hagenthon.backend.data.Question;
import it.hagenthon.backend.data.QuestionRepository;
import it.hagenthon.backend.data.QuestionType;
import it.hagenthon.backend.data.Voce;

/** RB-05, RB-06, RB-07, RB-24, RB-25: stato e salvataggio del profilo. */
@Service
public class ProfileService {

    private static final List<String> VOCI_ORDINE = List.of("CASA", "SPORT_TEMPO_LIBERO", "AUTO_MOBILITA", "UTENZE",
            "SPESA");

    private final ProfileRepository profileRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final AnswerRangeRepository answerRangeRepository;
    private final DataLoadRepository dataLoadRepository;

    public ProfileService(ProfileRepository profileRepository, QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository, AnswerRangeRepository answerRangeRepository,
            DataLoadRepository dataLoadRepository) {
        this.profileRepository = profileRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.answerRangeRepository = answerRangeRepository;
        this.dataLoadRepository = dataLoadRepository;
    }

    private void requireDatiCaricati() {
        if (dataLoadRepository.count() == 0) {
            throw new ApiException(409, "NESSUN_DATO", "Nessun dato caricato");
        }
    }

    @Transactional(readOnly = true)
    public ProfileResponse get(String nomeUtente) {
        requireDatiCaricati();
        List<Question> domande = questionRepository.findAllByOrderByOrdineAsc();

        List<DomandaDto> domandeDto = new ArrayList<>();
        Map<String, List<AnswerOption>> opzioniByDomanda = new LinkedHashMap<>();
        Map<String, List<AnswerRange>> fasceByDomanda = new LinkedHashMap<>();
        for (Question q : domande) {
            List<AnswerOption> opzioni = answerOptionRepository.findAllByQuestionOrderByOrdineAsc(q);
            List<AnswerRange> fasce = answerRangeRepository.findAllByQuestion(q);
            opzioniByDomanda.put(q.getCodice(), opzioni);
            fasceByDomanda.put(q.getCodice(), fasce);
            Integer minimo = null;
            Integer massimo = null;
            if (q.getTipo() == QuestionType.NUMERO) {
                minimo = (int) fasce.stream().mapToLong(AnswerRange::getMinimo).min().orElse(0);
                massimo = (int) fasce.stream().mapToLong(AnswerRange::getMassimo).max().orElse(0);
            }
            List<OpzioneDto> opzioniDto = opzioni.stream().map(o -> new OpzioneDto(o.getCodice(), o.getTesto()))
                    .toList();
            domandeDto.add(new DomandaDto(q.getCodice(), q.getTipo().name(), q.getEtichetta(), q.getTesto(),
                    q.getSpiegazione(), opzioniDto, minimo, massimo));
        }

        Optional<Profile> maybeProfile = profileRepository.findByNomeUtente(nomeUtente);
        if (maybeProfile.isEmpty()) {
            return new ProfileResponse("ASSENTE", domandeDto, VOCI_ORDINE, Map.of(), null, null);
        }

        Profile profile = maybeProfile.get();
        Map<String, String> risposteValide = new LinkedHashMap<>();
        boolean completo = true;
        for (Question q : domande) {
            String salvato = profile.getRisposte().get(q.getCodice());
            boolean valido = false;
            if (salvato != null) {
                if (q.getTipo() == QuestionType.SCELTA) {
                    valido = opzioniByDomanda.get(q.getCodice()).stream()
                            .anyMatch(o -> o.getCodice().equals(salvato));
                } else {
                    List<AnswerRange> fasce = fasceByDomanda.get(q.getCodice());
                    long allowedMin = fasce.stream().mapToLong(AnswerRange::getMinimo).min().orElse(0);
                    long allowedMax = fasce.stream().mapToLong(AnswerRange::getMassimo).max().orElse(0);
                    if (salvato.matches("[0-9]+")) {
                        BigInteger v = new BigInteger(salvato);
                        valido = v.compareTo(BigInteger.valueOf(allowedMin)) >= 0
                                && v.compareTo(BigInteger.valueOf(allowedMax)) <= 0;
                    }
                }
            }
            if (valido) {
                risposteValide.put(q.getCodice(), salvato);
            } else {
                completo = false;
            }
        }
        String stato = completo ? "COMPLETO" : "NON_PIU_COMPLETO";
        Map<String, Integer> importi = new LinkedHashMap<>();
        importi.put("CASA", profile.getCasa());
        importi.put("SPORT_TEMPO_LIBERO", profile.getSportTempoLibero());
        importi.put("AUTO_MOBILITA", profile.getAutoMobilita());
        importi.put("UTENZE", profile.getUtenze());
        importi.put("SPESA", profile.getSpesa());

        return new ProfileResponse(stato, domandeDto, VOCI_ORDINE, risposteValide, importi,
                Timestamps.format(profile.getSalvatoIl()));
    }

    @Transactional
    public SalvataggioResponse save(String nomeUtente, Map<String, String> risposteRaw, Map<String, String> importiRaw) {
        requireDatiCaricati();
        List<Question> domande = questionRepository.findAllByOrderByOrdineAsc();
        List<ErrorDetail> dettagli = new ArrayList<>();
        Map<String, String> risposteValidate = new LinkedHashMap<>();

        for (Question q : domande) {
            String raw = risposteRaw == null ? null : risposteRaw.get(q.getCodice());
            if (q.getTipo() == QuestionType.SCELTA) {
                List<AnswerOption> opzioni = answerOptionRepository.findAllByQuestionOrderByOrdineAsc(q);
                ProfileRules.Risultato<String> risultato = ProfileRules.validateScelta(raw, opzioni);
                if (!risultato.valido()) {
                    dettagli.add(new ErrorDetail("risposte." + q.getCodice(), risultato.codiceErrore(),
                            risultato.minimo(), risultato.massimo()));
                } else {
                    risposteValidate.put(q.getCodice(), risultato.valore());
                }
            } else {
                List<AnswerRange> fasce = answerRangeRepository.findAllByQuestion(q);
                ProfileRules.Risultato<Long> risultato = ProfileRules.validateNumero(raw, fasce);
                if (!risultato.valido()) {
                    dettagli.add(new ErrorDetail("risposte." + q.getCodice(), risultato.codiceErrore(),
                            risultato.minimo(), risultato.massimo()));
                } else {
                    risposteValidate.put(q.getCodice(), String.valueOf(risultato.valore()));
                }
            }
        }

        Map<String, Integer> importiValidati = new LinkedHashMap<>();
        for (String voce : VOCI_ORDINE) {
            String raw = importiRaw == null ? null : importiRaw.get(voce);
            ProfileRules.Risultato<Integer> risultato = ProfileRules.validateAmount(raw);
            if (!risultato.valido()) {
                dettagli.add(new ErrorDetail("importi." + voce, risultato.codiceErrore(), risultato.minimo(),
                        risultato.massimo()));
            } else {
                importiValidati.put(voce, risultato.valore());
            }
        }

        if (!dettagli.isEmpty()) {
            throw new ApiException(400, "VALIDAZIONE", "Dati del profilo non validi", dettagli);
        }

        Profile profile = profileRepository.findByNomeUtente(nomeUtente).orElseGet(Profile::new);
        profile.setNomeUtente(nomeUtente);
        profile.setRisposte(risposteValidate);
        profile.setCasa(importiValidati.get("CASA"));
        profile.setSportTempoLibero(importiValidati.get("SPORT_TEMPO_LIBERO"));
        profile.setAutoMobilita(importiValidati.get("AUTO_MOBILITA"));
        profile.setUtenze(importiValidati.get("UTENZE"));
        profile.setSpesa(importiValidati.get("SPESA"));
        LocalDateTime adesso = Timestamps.now();
        profile.setSalvatoIl(adesso);
        profileRepository.save(profile);

        return new SalvataggioResponse("COMPLETO", Timestamps.format(adesso));
    }
}
