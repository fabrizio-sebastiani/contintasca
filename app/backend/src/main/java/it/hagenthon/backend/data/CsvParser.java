package it.hagenthon.backend.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import it.hagenthon.backend.common.ApiException;

/**
 * Lettura e validazione del file dati ISTAT (fittizio). Regole alla lettera di
 * {@code agents/skills/formato-csv-istat/SKILL.md}. Non tocca il database: restituisce
 * un risultato in memoria che {@code DataImportService} persiste solo se accettato.
 */
@Component
public class CsvParser {

    private static final String INTESTAZIONE =
            "tipo_riga;domanda;tipo_risposta;etichetta;testo;spiegazione;opzione;minimo;massimo;punteggio;fascia;voce;importo";

    public CsvParseResult parse(byte[] bytes) {
        String decoded = stripBom(decodeUtf8(bytes));
        String[] lines = decoded.split("\r\n|\n|\r", -1);

        boolean tuttoVuoto = true;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                tuttoVuoto = false;
                break;
            }
        }
        if (tuttoVuoto) {
            throw new ApiException(400, "FILE_VUOTO", "File vuoto");
        }

        String header = lines[0].trim();
        if (!header.equalsIgnoreCase(INTESTAZIONE)) {
            throw new ApiException(400, "FILE_INTESTAZIONE_NON_VALIDA", "Intestazione non valida");
        }

        CsvParseResult result = new CsvParseResult();
        Map<String, ParsedQuestion> domande = new LinkedHashMap<>();
        Map<String, ParsedScoreBand> fasce = new LinkedHashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            int riga = i + 1;
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] raw = line.split(";", -1);
            if (raw.length == 13 && allBlank(raw)) {
                continue;
            }
            result.righeLette++;
            try {
                processRow(raw, riga, domande, fasce);
            } catch (ScartoRigaException e) {
                result.scarti.add(new CsvScarto(riga, e.codice));
            }
        }

        List<ParsedScoreBand> fasceComplete = new ArrayList<>();
        for (ParsedScoreBand band : fasce.values()) {
            if (band.importi.size() == 5) {
                fasceComplete.add(band);
            } else {
                result.scarti.add(new CsvScarto(band.riga, "FASCIA_INCOMPLETA"));
                for (int rigaImporto : band.righeImporti.values()) {
                    result.scarti.add(new CsvScarto(rigaImporto, "FASCIA_INCOMPLETA"));
                }
            }
        }

        List<ParsedQuestion> domandeValide = new ArrayList<>();
        for (ParsedQuestion q : domande.values()) {
            boolean haRisposte = q.tipo == QuestionType.SCELTA ? !q.opzioni.isEmpty() : !q.fasceRisposta.isEmpty();
            if (haRisposte) {
                domandeValide.add(q);
            } else {
                result.scarti.add(new CsvScarto(q.riga, "DOMANDA_SENZA_RISPOSTE"));
            }
        }

        result.scarti.sort(Comparator.comparingInt(CsvScarto::riga));
        result.righeScartate = result.scarti.size();
        result.righeImportate = result.righeLette - result.righeScartate;
        result.domande.addAll(domandeValide);
        result.fasce.addAll(fasceComplete);

        if (domandeValide.isEmpty() || fasceComplete.isEmpty()) {
            throw new ApiException(400, "FILE_SENZA_DATI_VALIDI", "File senza dati validi");
        }

        return result;
    }

    private void processRow(String[] raw, int riga, Map<String, ParsedQuestion> domande,
            Map<String, ParsedScoreBand> fasce) {
        if (raw.length != 13) {
            throw new ScartoRigaException("NUMERO_COLONNE_ERRATO");
        }
        String[] f = new String[13];
        for (int i = 0; i < 13; i++) {
            f[i] = raw[i] == null ? "" : raw[i].trim();
        }
        String tipoRiga = f[0].toUpperCase(Locale.ROOT);
        switch (tipoRiga) {
            case "DOMANDA" -> processDomanda(f, riga, domande);
            case "OPZIONE" -> processOpzione(f, riga, domande);
            case "FASCIA_RISPOSTA" -> processFasciaRisposta(f, riga, domande);
            case "FASCIA_PUNTEGGIO" -> processFasciaPunteggio(f, riga, fasce);
            case "SPESA" -> processSpesa(f, riga, fasce);
            default -> throw new ScartoRigaException("TIPO_RIGA_SCONOSCIUTO");
        }
    }

    private void processDomanda(String[] f, int riga, Map<String, ParsedQuestion> domande) {
        requireNonEmpty(f[1]);
        requireNonEmpty(f[2]);
        requireNonEmpty(f[3]);
        requireNonEmpty(f[4]);
        checkCodice(f[1]);
        checkLength(f[3], 40);
        checkLength(f[4], 200);
        if (!f[5].isEmpty()) {
            checkLength(f[5], 200);
        }
        String tipoStr = f[2].toUpperCase(Locale.ROOT);
        QuestionType tipo;
        if (tipoStr.equals("SCELTA")) {
            tipo = QuestionType.SCELTA;
        } else if (tipoStr.equals("NUMERO")) {
            tipo = QuestionType.NUMERO;
        } else {
            throw new ScartoRigaException("TIPO_RISPOSTA_SCONOSCIUTO");
        }
        String codice = f[1].toUpperCase(Locale.ROOT);
        if (domande.containsKey(codice)) {
            throw new ScartoRigaException("CODICE_DUPLICATO");
        }
        ParsedQuestion q = new ParsedQuestion();
        q.codice = codice;
        q.tipo = tipo;
        q.etichetta = f[3];
        q.testo = f[4];
        q.spiegazione = f[5].isEmpty() ? null : f[5];
        q.riga = riga;
        domande.put(codice, q);
    }

    private void processOpzione(String[] f, int riga, Map<String, ParsedQuestion> domande) {
        requireNonEmpty(f[1]);
        requireNonEmpty(f[4]);
        requireNonEmpty(f[6]);
        requireNonEmpty(f[9]);
        checkCodice(f[1]);
        checkLength(f[4], 100);
        checkCodice(f[6]);
        long punteggio = parseBoundedInteger(f[9], 1000L);
        String domandaCodice = f[1].toUpperCase(Locale.ROOT);
        ParsedQuestion question = domande.get(domandaCodice);
        if (question == null) {
            throw new ScartoRigaException("RIF_DOMANDA_INESISTENTE");
        }
        if (question.tipo != QuestionType.SCELTA) {
            throw new ScartoRigaException("TIPO_RISPOSTA_NON_COERENTE");
        }
        String opzioneCodice = f[6].toUpperCase(Locale.ROOT);
        for (ParsedOption existing : question.opzioni) {
            if (existing.codice.equals(opzioneCodice)) {
                throw new ScartoRigaException("CODICE_DUPLICATO");
            }
        }
        ParsedOption opt = new ParsedOption();
        opt.codice = opzioneCodice;
        opt.testo = f[4];
        opt.punteggio = (int) punteggio;
        opt.riga = riga;
        opt.ordine = question.opzioni.size();
        question.opzioni.add(opt);
    }

    private void processFasciaRisposta(String[] f, int riga, Map<String, ParsedQuestion> domande) {
        requireNonEmpty(f[1]);
        requireNonEmpty(f[7]);
        requireNonEmpty(f[8]);
        requireNonEmpty(f[9]);
        checkCodice(f[1]);
        long minimo = parseBoundedInteger(f[7], 999999999L);
        long massimo = parseBoundedInteger(f[8], 999999999L);
        long punteggio = parseBoundedInteger(f[9], 1000L);
        if (minimo > massimo) {
            throw new ScartoRigaException("MINIMO_MAGGIORE_MASSIMO");
        }
        String domandaCodice = f[1].toUpperCase(Locale.ROOT);
        ParsedQuestion question = domande.get(domandaCodice);
        if (question == null) {
            throw new ScartoRigaException("RIF_DOMANDA_INESISTENTE");
        }
        if (question.tipo != QuestionType.NUMERO) {
            throw new ScartoRigaException("TIPO_RISPOSTA_NON_COERENTE");
        }
        for (ParsedRange existing : question.fasceRisposta) {
            if (minimo <= existing.massimo && existing.minimo <= massimo) {
                throw new ScartoRigaException("FASCIA_SOVRAPPOSTA");
            }
        }
        ParsedRange range = new ParsedRange();
        range.minimo = minimo;
        range.massimo = massimo;
        range.punteggio = (int) punteggio;
        range.riga = riga;
        question.fasceRisposta.add(range);
    }

    private void processFasciaPunteggio(String[] f, int riga, Map<String, ParsedScoreBand> fasce) {
        requireNonEmpty(f[7]);
        requireNonEmpty(f[8]);
        requireNonEmpty(f[10]);
        checkCodice(f[10]);
        long minimo = parseBoundedInteger(f[7], 999999999L);
        long massimo = parseBoundedInteger(f[8], 999999999L);
        if (minimo > massimo) {
            throw new ScartoRigaException("MINIMO_MAGGIORE_MASSIMO");
        }
        String codice = f[10].toUpperCase(Locale.ROOT);
        if (fasce.containsKey(codice)) {
            throw new ScartoRigaException("CODICE_DUPLICATO");
        }
        for (ParsedScoreBand existing : fasce.values()) {
            if (minimo <= existing.massimo && existing.minimo <= massimo) {
                throw new ScartoRigaException("FASCIA_SOVRAPPOSTA");
            }
        }
        ParsedScoreBand band = new ParsedScoreBand();
        band.codice = codice;
        band.minimo = (int) minimo;
        band.massimo = (int) massimo;
        band.riga = riga;
        fasce.put(codice, band);
    }

    private void processSpesa(String[] f, int riga, Map<String, ParsedScoreBand> fasce) {
        requireNonEmpty(f[10]);
        requireNonEmpty(f[11]);
        requireNonEmpty(f[12]);
        checkCodice(f[10]);
        String voceStr = f[11].toUpperCase(Locale.ROOT);
        Voce voce;
        try {
            voce = Voce.valueOf(voceStr);
        } catch (IllegalArgumentException e) {
            throw new ScartoRigaException("VOCE_SCONOSCIUTA");
        }
        BigDecimal importo = parseImporto(f[12]);
        String fasciaCodice = f[10].toUpperCase(Locale.ROOT);
        ParsedScoreBand band = fasce.get(fasciaCodice);
        if (band == null) {
            throw new ScartoRigaException("RIF_FASCIA_INESISTENTE");
        }
        if (band.importi.containsKey(voce)) {
            throw new ScartoRigaException("CODICE_DUPLICATO");
        }
        band.importi.put(voce, importo);
        band.righeImporti.put(voce, riga);
    }

    private static void requireNonEmpty(String v) {
        if (v.isEmpty()) {
            throw new ScartoRigaException("CAMPO_MANCANTE");
        }
    }

    private static void checkLength(String v, int max) {
        if (v.length() > max) {
            throw new ScartoRigaException("TESTO_TROPPO_LUNGO");
        }
    }

    private static void checkCodice(String v) {
        if (!v.matches("[A-Za-z0-9_]{1,30}")) {
            throw new ScartoRigaException("CODICE_NON_VALIDO");
        }
    }

    private static long parseBoundedInteger(String raw, long max) {
        String v = raw.trim();
        String body = v.startsWith("-") ? v.substring(1) : v;
        if (!body.matches("[0-9]+")) {
            throw new ScartoRigaException("NUMERO_NON_VALIDO");
        }
        if (v.startsWith("-")) {
            throw new ScartoRigaException("VALORE_NEGATIVO");
        }
        BigInteger bi = new BigInteger(body);
        if (bi.compareTo(BigInteger.valueOf(max)) > 0) {
            throw new ScartoRigaException("NUMERO_NON_VALIDO");
        }
        return bi.longValueExact();
    }

    private static BigDecimal parseImporto(String raw) {
        String v = raw.trim();
        String body = v.startsWith("-") ? v.substring(1) : v;
        if (!body.matches("[0-9]+([.,][0-9]{1,2})?")) {
            throw new ScartoRigaException("NUMERO_NON_VALIDO");
        }
        if (v.startsWith("-")) {
            throw new ScartoRigaException("VALORE_NEGATIVO");
        }
        BigDecimal value = new BigDecimal(body.replace(',', '.'));
        if (value.compareTo(new BigDecimal("999999.99")) > 0) {
            throw new ScartoRigaException("NUMERO_NON_VALIDO");
        }
        return value;
    }

    private static boolean allBlank(String[] fields) {
        for (String f : fields) {
            if (!f.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String decodeUtf8(byte[] bytes) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            throw new ApiException(400, "FILE_CODIFICA_NON_VALIDA", "Codifica non valida");
        }
    }

    private static String stripBom(String s) {
        if (!s.isEmpty() && s.charAt(0) == '﻿') {
            return s.substring(1);
        }
        return s;
    }
}
