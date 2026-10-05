package it.hagenthon.backend.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.hagenthon.backend.analysis.ComparisonService.RigaConfronto;
import it.hagenthon.backend.data.ScoreBand;

/** RB-09…RB-13: calcolo puro del confronto (CA-05 … CA-09). */
class ComparisonServiceTest {

    @Test
    void autoSopra24() {
        RigaConfronto r = ComparisonService.compare(6200, 5000);
        assertEquals(1200, r.differenza());
        assertEquals(1200, r.differenzaAssoluta());
        assertEquals(24, r.percentuale());
        assertEquals("SOPRA", r.esito());
        assertEquals("SOPRA_PERCENTUALE", r.fraseCifra());
    }

    @Test
    void totale() {
        RigaConfronto r = ComparisonService.compare(33600, 33000);
        assertEquals(600, r.differenza());
        assertEquals(2, r.percentuale());
        assertEquals("IN_LINEA", r.esito());
        assertEquals("IN_LINEA_PIU", r.fraseCifra());
    }

    @Test
    void soglieUtenze() {
        assertEquals("IN_LINEA", ComparisonService.compare(5500, 5000).esito());
        RigaConfronto sopra = ComparisonService.compare(5501, 5000);
        assertEquals("SOPRA", sopra.esito());
        assertEquals(10, sopra.percentuale());
        assertEquals("SOPRA_OLTRE_10", sopra.fraseCifra());

        assertEquals("IN_LINEA", ComparisonService.compare(4500, 5000).esito());
        assertEquals("IN_LINEA_MENO", ComparisonService.compare(4500, 5000).fraseCifra());

        RigaConfronto sotto = ComparisonService.compare(4499, 5000);
        assertEquals("SOTTO", sotto.esito());
        assertEquals(10, sotto.percentuale());
        assertEquals("SOTTO_OLTRE_10", sotto.fraseCifra());

        RigaConfronto uguale = ComparisonService.compare(5000, 5000);
        assertEquals("IN_LINEA", uguale.esito());
        assertEquals("IN_LINEA_UGUALE", uguale.fraseCifra());
        assertEquals(0, uguale.differenza());
    }

    @Test
    void campioneZero() {
        RigaConfronto r = ComparisonService.compare(100, 0);
        assertEquals("SOPRA", r.esito());
        assertEquals("SOPRA_CAMPIONE_ZERO", r.fraseCifra());
        assertEquals(null, r.percentuale());

        RigaConfronto zero = ComparisonService.compare(0, 0);
        assertEquals("IN_LINEA", zero.esito());
        assertEquals("IN_LINEA_UGUALE", zero.fraseCifra());
    }

    @Test
    void arrotondamentoEuroDaCinquantaInSu() {
        assertEquals(4201, ComparisonService.roundEuro(new BigDecimal("4200.50")));
        assertEquals(4200, ComparisonService.roundEuro(new BigDecimal("4200.49")));
    }

    @Test
    void chooseBandEsatta() {
        ScoreBand f1 = banda("F1", 6, 10);
        ScoreBand f2 = banda("F2", 11, 13);
        ScoreBand f3 = banda("F3", 14, 18);
        ScoreBand f4 = banda("F4", 23, 25);
        NearestRange.Esito<ScoreBand> esito = ComparisonService.chooseBand(List.of(f1, f2, f3, f4), 15);
        assertTrue(esito.dentro());
        assertEquals("F3", esito.scelta().valore().getCodice());
    }

    @Test
    void chooseBandApprossimata() {
        ScoreBand f1 = banda("F1", 6, 10);
        ScoreBand f2 = banda("F2", 11, 13);
        ScoreBand f3 = banda("F3", 14, 18);
        ScoreBand f4 = banda("F4", 23, 25);
        NearestRange.Esito<ScoreBand> esito = ComparisonService.chooseBand(List.of(f1, f2, f3, f4), 21);
        assertFalse(esito.dentro());
        assertEquals("F4", esito.scelta().valore().getCodice());
    }

    private static ScoreBand banda(String codice, int minimo, int massimo) {
        ScoreBand b = new ScoreBand();
        b.setCodice(codice);
        b.setMinimo(minimo);
        b.setMassimo(massimo);
        return b;
    }
}
