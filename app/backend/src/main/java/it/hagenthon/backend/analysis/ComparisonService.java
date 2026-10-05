package it.hagenthon.backend.analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.hagenthon.backend.common.ApiException;
import it.hagenthon.backend.data.AnswerOption;
import it.hagenthon.backend.data.AnswerOptionRepository;
import it.hagenthon.backend.data.AnswerRange;
import it.hagenthon.backend.data.AnswerRangeRepository;
import it.hagenthon.backend.data.Question;
import it.hagenthon.backend.data.QuestionRepository;
import it.hagenthon.backend.data.QuestionType;
import it.hagenthon.backend.data.SampleAmount;
import it.hagenthon.backend.data.SampleAmountRepository;
import it.hagenthon.backend.data.ScoreBand;
import it.hagenthon.backend.data.ScoreBandRepository;
import it.hagenthon.backend.data.Voce;
import it.hagenthon.backend.profile.ProfileResponse;
import it.hagenthon.backend.profile.ProfileRules;
import it.hagenthon.backend.profile.ProfileService;

/**
 * RB-09…RB-13, RB-15, RB-18: punteggio del profilo, fascia, confronto voce per voce e totale.
 * I metodi statici sono puri (nessuna dipendenza da Spring) e si possono testare direttamente.
 */
@Service
public class ComparisonService {

    private final ProfileService profileService;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final AnswerRangeRepository answerRangeRepository;
    private final ScoreBandRepository scoreBandRepository;
    private final SampleAmountRepository sampleAmountRepository;

    public ComparisonService(ProfileService profileService, QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository, AnswerRangeRepository answerRangeRepository,
            ScoreBandRepository scoreBandRepository, SampleAmountRepository sampleAmountRepository) {
        this.profileService = profileService;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.answerRangeRepository = answerRangeRepository;
        this.scoreBandRepository = scoreBandRepository;
        this.sampleAmountRepository = sampleAmountRepository;
    }

    /** RB-11: arrotondamento all'euro, da ,50 in su per eccesso. */
    public static int roundEuro(BigDecimal importo) {
        return importo.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    public record RigaConfronto(int spesaCorrente, int spesaCampione, int differenza, int differenzaAssoluta,
            Integer percentuale, String esito, String fraseCifra) {
    }

    /** RB-12, RB-13, RB-17 (tabella B): differenza, percentuale, esito e frase. */
    public static RigaConfronto compare(int corrente, int campione) {
        int differenza = corrente - campione;
        int differenzaAssoluta = Math.abs(differenza);
        Integer percentuale = campione == 0 ? null : percentualeArrotondata(differenzaAssoluta, campione);

        String esito;
        if (differenza == 0) {
            esito = "IN_LINEA";
        } else if (campione == 0) {
            esito = "SOPRA";
        } else if ((long) differenzaAssoluta * 10 <= campione) {
            esito = "IN_LINEA";
        } else {
            esito = differenza > 0 ? "SOPRA" : "SOTTO";
        }

        String fraseCifra = fraseCifra(esito, differenza, percentuale, campione);
        return new RigaConfronto(corrente, campione, differenza, differenzaAssoluta, percentuale, esito, fraseCifra);
    }

    private static Integer percentualeArrotondata(int differenzaAssoluta, int campione) {
        BigDecimal numeratore = BigDecimal.valueOf((long) differenzaAssoluta * 100);
        BigDecimal denominatore = BigDecimal.valueOf(campione);
        return numeratore.divide(denominatore, 0, RoundingMode.HALF_UP).intValueExact();
    }

    private static String fraseCifra(String esito, int differenza, Integer percentuale, int campione) {
        if ("SOPRA".equals(esito) && campione == 0) {
            return "SOPRA_CAMPIONE_ZERO";
        }
        if ("SOPRA".equals(esito)) {
            return percentuale != null && percentuale == 10 ? "SOPRA_OLTRE_10" : "SOPRA_PERCENTUALE";
        }
        if ("SOTTO".equals(esito)) {
            return percentuale != null && percentuale == 10 ? "SOTTO_OLTRE_10" : "SOTTO_PERCENTUALE";
        }
        if (differenza == 0) {
            return "IN_LINEA_UGUALE";
        }
        return differenza > 0 ? "IN_LINEA_PIU" : "IN_LINEA_MENO";
    }

    /** RB-09, RB-10: fascia esatta o più vicina a parità di distanza (minimo più basso). */
    public static NearestRange.Esito<ScoreBand> chooseBand(List<ScoreBand> bande, int punteggio) {
        List<NearestRange.RangeItem<ScoreBand>> items = bande.stream()
                .map(b -> new NearestRange.RangeItem<>((long) b.getMinimo(), (long) b.getMassimo(), b))
                .toList();
        return NearestRange.pick(items, punteggio);
    }

    @Transactional(readOnly = true)
    public AnalisiResponse analizza(String nomeUtente) {
        ProfileResponse profilo = profileService.get(nomeUtente);
        if ("ASSENTE".equals(profilo.statoProfilo())) {
            throw new ApiException(409, "PROFILO_ASSENTE", "Profilo assente");
        }
        if ("NON_PIU_COMPLETO".equals(profilo.statoProfilo())) {
            throw new ApiException(409, "PROFILO_NON_PIU_COMPLETO", "Profilo non più completo");
        }

        List<Question> domande = questionRepository.findAllByOrderByOrdineAsc();
        int punteggio = 0;
        List<RispostaMostrataDto> risposteDto = new ArrayList<>();
        for (Question q : domande) {
            String valore = profilo.risposte().get(q.getCodice());
            if (q.getTipo() == QuestionType.SCELTA) {
                List<AnswerOption> opzioni = answerOptionRepository.findAllByQuestionOrderByOrdineAsc(q);
                AnswerOption scelta = opzioni.stream().filter(o -> o.getCodice().equals(valore)).findFirst()
                        .orElseThrow(() -> new ApiException(500, "ERRORE_INTERNO", "Risposta non coerente"));
                punteggio += ProfileRules.scoreScelta(scelta);
                risposteDto.add(new RispostaMostrataDto(q.getEtichetta(), "SCELTA", scelta.getTesto(), null));
            } else {
                List<AnswerRange> fasce = answerRangeRepository.findAllByQuestion(q);
                long v = Long.parseLong(valore);
                punteggio += ProfileRules.scoreNumero(fasce, v);
                risposteDto.add(new RispostaMostrataDto(q.getEtichetta(), "NUMERO", null, (int) v));
            }
        }

        List<ScoreBand> bande = scoreBandRepository.findAll();
        NearestRange.Esito<ScoreBand> esitoFascia = chooseBand(bande, punteggio);
        ScoreBand fasciaScelta = esitoFascia.scelta().valore();
        boolean approssimato = !esitoFascia.dentro();

        List<SampleAmount> importiCampione = sampleAmountRepository.findAllByBand(fasciaScelta);
        Map<Voce, Integer> campioneArrotondato = new EnumMap<>(Voce.class);
        for (SampleAmount sa : importiCampione) {
            campioneArrotondato.put(sa.getVoce(), roundEuro(sa.getImporto()));
        }

        Map<String, Integer> importiCorrente = profilo.importi();
        List<VoceConfrontoDto> vociDto = new ArrayList<>();
        int totaleCorrente = 0;
        int totaleCampione = 0;
        int scalaMassima = 0;
        for (Voce voce : Voce.values()) {
            int corrente = importiCorrente.get(voce.name());
            int campione = campioneArrotondato.getOrDefault(voce, 0);
            totaleCorrente += corrente;
            totaleCampione += campione;
            scalaMassima = Math.max(scalaMassima, Math.max(corrente, campione));
            RigaConfronto riga = compare(corrente, campione);
            vociDto.add(new VoceConfrontoDto(voce.name(), riga.spesaCorrente(), riga.spesaCampione(),
                    riga.differenza(), riga.differenzaAssoluta(), riga.percentuale(), riga.esito(),
                    riga.fraseCifra()));
        }
        RigaConfronto totaleConfronto = compare(totaleCorrente, totaleCampione);
        VoceConfrontoDto totaleDto = new VoceConfrontoDto("TOTALE", totaleConfronto.spesaCorrente(),
                totaleConfronto.spesaCampione(), totaleConfronto.differenza(), totaleConfronto.differenzaAssoluta(),
                totaleConfronto.percentuale(), totaleConfronto.esito(), totaleConfronto.fraseCifra());

        FasciaDto fasciaDto = new FasciaDto(fasciaScelta.getCodice(), fasciaScelta.getMinimo(),
                fasciaScelta.getMassimo());

        return new AnalisiResponse(punteggio, fasciaDto, approssimato, risposteDto, vociDto, totaleDto, scalaMassima);
    }
}
