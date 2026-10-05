package it.hagenthon.backend.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.hagenthon.backend.analysis.NearestRange.Esito;
import it.hagenthon.backend.analysis.NearestRange.RangeItem;

/** RB-07, RB-10: la fascia più vicina a parità di distanza sceglie il minimo più basso. */
class NearestRangeTest {

    private static final List<RangeItem<String>> FASCE = List.of(
            new RangeItem<>(6, 10, "F1"),
            new RangeItem<>(11, 13, "F2"),
            new RangeItem<>(14, 18, "F3"),
            new RangeItem<>(23, 25, "F4"));

    @Test
    void valoreDentroUnaFascia() {
        Esito<String> esito = NearestRange.pick(FASCE, 15);
        assertTrue(esito.dentro());
        assertEquals("F3", esito.scelta().valore());
    }

    @Test
    void valoreVicinoAFasciaFuoriDaTutte() {
        Esito<String> esito = NearestRange.pick(FASCE, 21);
        assertFalse(esito.dentro());
        assertEquals("F4", esito.scelta().valore());
    }

    @Test
    void parita_sceglieIlMinimoPiuBasso() {
        List<RangeItem<String>> fasce = List.of(
                new RangeItem<>(0, 5, "BASSA"),
                new RangeItem<>(10, 15, "ALTA"));
        // valore 7: distanza da BASSA = 7-5 = 2, distanza da ALTA = 10-7 = 3 -> non è parità, forziamo la parità:
        Esito<String> esitoParita = NearestRange.pick(fasce, 8); // distanza da BASSA=3, da ALTA=2 -> ALTA vince
        assertEquals("ALTA", esitoParita.scelta().valore());

        List<RangeItem<String>> fasceParita = List.of(
                new RangeItem<>(0, 5, "BASSA"),
                new RangeItem<>(11, 16, "ALTA"));
        // valore 8: distanza da BASSA = 3, da ALTA = 3 -> parità, vince il minimo più basso (BASSA)
        Esito<String> esito = NearestRange.pick(fasceParita, 8);
        assertFalse(esito.dentro());
        assertEquals("BASSA", esito.scelta().valore());
    }
}
