package it.hagenthon.backend.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.hagenthon.backend.common.ApiException;

/** CA-01: nome vuoto -> CAMPO_MANCANTE; 51 caratteri -> CAMPO_TROPPO_LUNGO 50; " Marco " -> "marco". */
class AccessControllerTest {

    private final AccessController controller = new AccessController();

    @Test
    void nomeUtenteVuoto() {
        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(ApiException.class,
                () -> controller.accedi(new AccessRequest("", "qualsiasi")));
        assertEquals("VALIDAZIONE", ex.getCodice());
        assertEquals("nomeUtente", ex.getDettagli().get(0).campo());
        assertEquals("CAMPO_MANCANTE", ex.getDettagli().get(0).codice());
    }

    @Test
    void nomeUtenteTroppoLungo() {
        String nome51 = "a".repeat(51);
        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(ApiException.class,
                () -> controller.accedi(new AccessRequest(nome51, "qualsiasi")));
        assertEquals("CAMPO_TROPPO_LUNGO", ex.getDettagli().get(0).codice());
        assertEquals(50, ex.getDettagli().get(0).massimo());
    }

    @Test
    void normalizzaSpaziEMinuscole() {
        AccessResponse resp = controller.accedi(new AccessRequest(" Marco ", "qualsiasi"));
        assertEquals("marco", resp.nomeUtente());
    }

    @Test
    void passwordMancante() {
        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(ApiException.class,
                () -> controller.accedi(new AccessRequest("marco", "")));
        assertEquals("password", ex.getDettagli().get(0).campo());
        assertEquals("CAMPO_MANCANTE", ex.getDettagli().get(0).codice());
    }
}
