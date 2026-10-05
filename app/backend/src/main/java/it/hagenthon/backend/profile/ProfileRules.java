package it.hagenthon.backend.profile;

import java.math.BigInteger;
import java.util.List;

import it.hagenthon.backend.analysis.NearestRange;
import it.hagenthon.backend.analysis.NearestRange.Esito;
import it.hagenthon.backend.analysis.NearestRange.RangeItem;
import it.hagenthon.backend.data.AnswerOption;
import it.hagenthon.backend.data.AnswerRange;

/** RB-06, RB-07, RB-08: regole di validazione e punteggio del profilo. Classe pura, senza Spring. */
public final class ProfileRules {

    private ProfileRules() {
    }

    public record Risultato<T>(boolean valido, String codiceErrore, Integer minimo, Integer massimo, T valore) {
        public static <T> Risultato<T> ok(T valore) {
            return new Risultato<>(true, null, null, null, valore);
        }

        public static <T> Risultato<T> errore(String codice) {
            return new Risultato<>(false, codice, null, null, null);
        }

        public static <T> Risultato<T> errore(String codice, int minimo, int massimo) {
            return new Risultato<>(false, codice, minimo, massimo, null);
        }
    }

    /** RB-06: importo annuale, intero 0-999999, solo cifre, zeri iniziali ignorati. */
    public static Risultato<Integer> validateAmount(String raw) {
        String v = raw == null ? "" : raw.trim();
        if (v.isEmpty()) {
            return Risultato.errore("IMPORTO_MANCANTE");
        }
        if (v.matches("-[0-9]+")) {
            return Risultato.errore("IMPORTO_NEGATIVO", 0, 999999);
        }
        if (!v.matches("[0-9]+")) {
            return Risultato.errore("IMPORTO_NON_CIFRE", 0, 999999);
        }
        BigInteger bi = new BigInteger(v);
        if (bi.compareTo(BigInteger.valueOf(999999)) > 0) {
            return Risultato.errore("IMPORTO_OLTRE_LIMITE", 0, 999999);
        }
        return Risultato.ok(bi.intValueExact());
    }

    /** Restituisce il codice canonico dell'opzione scelta, o un errore. */
    public static Risultato<String> validateScelta(String raw, List<AnswerOption> opzioni) {
        String v = raw == null ? "" : raw.trim();
        if (!v.isEmpty()) {
            for (AnswerOption o : opzioni) {
                if (o.getCodice().equalsIgnoreCase(v)) {
                    return Risultato.ok(o.getCodice());
                }
            }
        }
        return Risultato.errore("RISPOSTA_MANCANTE");
    }

    /** RB-07: valore numerico tra il minimo più basso e il massimo più alto delle fasce. */
    public static Risultato<Long> validateNumero(String raw, List<AnswerRange> fasce) {
        String v = raw == null ? "" : raw.trim();
        if (v.isEmpty()) {
            return Risultato.errore("RISPOSTA_MANCANTE");
        }
        long allowedMin = fasce.stream().mapToLong(AnswerRange::getMinimo).min().orElse(0);
        long allowedMax = fasce.stream().mapToLong(AnswerRange::getMassimo).max().orElse(0);
        if (!v.matches("[0-9]+")) {
            return Risultato.errore("NUMERO_NON_VALIDO", (int) allowedMin, (int) allowedMax);
        }
        BigInteger bi = new BigInteger(v);
        if (bi.compareTo(BigInteger.valueOf(allowedMin)) < 0 || bi.compareTo(BigInteger.valueOf(allowedMax)) > 0) {
            return Risultato.errore("NUMERO_NON_VALIDO", (int) allowedMin, (int) allowedMax);
        }
        return Risultato.ok(bi.longValueExact());
    }

    /** RB-08: punteggio di una risposta a scelta. */
    public static int scoreScelta(AnswerOption opzione) {
        return opzione.getPunteggio();
    }

    /** RB-08, RB-07: punteggio della fascia che contiene il valore (o la più vicina). */
    public static int scoreNumero(List<AnswerRange> fasce, long valore) {
        List<RangeItem<Integer>> items = fasce.stream()
                .map(r -> new RangeItem<>(r.getMinimo(), r.getMassimo(), r.getPunteggio()))
                .toList();
        Esito<Integer> esito = NearestRange.pick(items, valore);
        return esito.scelta().valore();
    }
}
