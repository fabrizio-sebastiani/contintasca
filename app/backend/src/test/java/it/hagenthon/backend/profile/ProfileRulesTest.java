package it.hagenthon.backend.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** RB-06: regole di validazione degli importi (CA-04). */
class ProfileRulesTest {

    @Test
    void zeriInizialiIgnorati() {
        ProfileRules.Risultato<Integer> r = ProfileRules.validateAmount("0120");
        assertTrue(r.valido());
        assertEquals(120, r.valore());
    }

    @Test
    void spaziAiBordiIgnorati() {
        ProfileRules.Risultato<Integer> r = ProfileRules.validateAmount("  12000  ");
        assertTrue(r.valido());
        assertEquals(12000, r.valore());
    }

    @Test
    void oltreIlLimite() {
        ProfileRules.Risultato<Integer> r = ProfileRules.validateAmount("1000000");
        assertFalse(r.valido());
        assertEquals("IMPORTO_OLTRE_LIMITE", r.codiceErrore());
        assertEquals(0, r.minimo());
        assertEquals(999999, r.massimo());
    }

    @Test
    void negativo() {
        ProfileRules.Risultato<Integer> r = ProfileRules.validateAmount("-100");
        assertFalse(r.valido());
        assertEquals("IMPORTO_NEGATIVO", r.codiceErrore());
    }

    @Test
    void conPunti() {
        ProfileRules.Risultato<Integer> r = ProfileRules.validateAmount("12.000");
        assertFalse(r.valido());
        assertEquals("IMPORTO_NON_CIFRE", r.codiceErrore());
    }

    @Test
    void mancante() {
        ProfileRules.Risultato<Integer> r = ProfileRules.validateAmount("");
        assertFalse(r.valido());
        assertEquals("IMPORTO_MANCANTE", r.codiceErrore());
        r = ProfileRules.validateAmount(null);
        assertFalse(r.valido());
        assertEquals("IMPORTO_MANCANTE", r.codiceErrore());
    }
}
