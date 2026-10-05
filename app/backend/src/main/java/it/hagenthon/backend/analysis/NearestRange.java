package it.hagenthon.backend.analysis;

import java.util.List;

/**
 * Sceglie l'intervallo che contiene un valore oppure, se nessuno lo contiene, quello con la
 * distanza minima dall'estremo più vicino; a parità, l'intervallo con il minimo più basso
 * (RB-07, RB-10). Utility pura e generica: usata sia per le risposte NUMERO del profilo, sia
 * per le fasce di punteggio del confronto.
 */
public final class NearestRange {

    private NearestRange() {
    }

    public record RangeItem<T>(long minimo, long massimo, T valore) {
    }

    /** {@code true} se il valore scelto cade esattamente dentro una delle fasce. */
    public record Esito<T>(RangeItem<T> scelta, boolean dentro) {
    }

    public static <T> Esito<T> pick(List<RangeItem<T>> fasce, long valore) {
        for (RangeItem<T> item : fasce) {
            if (valore >= item.minimo() && valore <= item.massimo()) {
                return new Esito<>(item, true);
            }
        }
        RangeItem<T> migliore = null;
        long distanzaMigliore = Long.MAX_VALUE;
        for (RangeItem<T> item : fasce) {
            long distanza = valore < item.minimo() ? item.minimo() - valore : valore - item.massimo();
            if (migliore == null || distanza < distanzaMigliore
                    || (distanza == distanzaMigliore && item.minimo() < migliore.minimo())) {
                migliore = item;
                distanzaMigliore = distanza;
            }
        }
        return new Esito<>(migliore, false);
    }
}
