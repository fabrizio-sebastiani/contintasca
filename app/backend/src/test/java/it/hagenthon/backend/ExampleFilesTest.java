package it.hagenthon.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.hagenthon.backend.analysis.AnalisiResponse;
import it.hagenthon.backend.analysis.ComparisonService;
import it.hagenthon.backend.data.DataImportService;
import it.hagenthon.backend.profile.ProfileService;

/**
 * CA-09, CA-10: carica il file di esempio e verifica il punteggio e la fascia dei due profili
 * attesi riportati in {@code agents/skills/formato-csv-istat/SKILL.md}.
 */
@SpringBootTest
class ExampleFilesTest {

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ComparisonService comparisonService;

    private void caricaFileEsempio() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/esempio_istat.csv")) {
            byte[] bytes = in.readAllBytes();
            dataImportService.importFile("esempio_istat.csv", bytes);
        }
    }

    @Test
    void profiloDiMarco_puntoggio15_fasciaF3() throws Exception {
        caricaFileEsempio();

        Map<String, String> risposte = new HashMap<>();
        risposte.put("PROVINCIA", "MI");
        risposte.put("ETA", "34");
        risposte.put("ABITAZIONE", "AFFITTO");
        risposte.put("SESSO", "M");
        risposte.put("REDDITO", "28000");
        risposte.put("SPORT", "SI");
        risposte.put("AUTOVETTURE", "1");
        risposte.put("CICLOMOTORI", "0");
        risposte.put("NUCLEO", "1");

        Map<String, String> importi = Map.of(
                "CASA", "12000",
                "SPORT_TEMPO_LIBERO", "2400",
                "AUTO_MOBILITA", "6200",
                "UTENZE", "5000",
                "SPESA", "8000");

        profileService.save("marco", risposte, importi);
        AnalisiResponse analisi = comparisonService.analizza("marco");

        assertEquals(15, analisi.punteggio());
        assertEquals("F3", analisi.fascia().codice());
        assertEquals(14, analisi.fascia().minimo());
        assertEquals(18, analisi.fascia().massimo());
        assertFalse(analisi.approssimato());
    }

    @Test
    void profiloFuoriFascia_punteggio21_fasciaApprossimataF4() throws Exception {
        caricaFileEsempio();

        Map<String, String> risposte = new HashMap<>();
        risposte.put("PROVINCIA", "MI");
        risposte.put("ETA", "70");
        risposte.put("ABITAZIONE", "PROPRIETA");
        risposte.put("SESSO", "F");
        risposte.put("REDDITO", "150000");
        risposte.put("SPORT", "NO");
        risposte.put("AUTOVETTURE", "3");
        risposte.put("CICLOMOTORI", "2");
        risposte.put("NUCLEO", "6");

        Map<String, String> importi = Map.of(
                "CASA", "9000",
                "SPORT_TEMPO_LIBERO", "2500",
                "AUTO_MOBILITA", "7500",
                "UTENZE", "6500",
                "SPESA", "11000");

        profileService.save("fuorifascia", risposte, importi);
        AnalisiResponse analisi = comparisonService.analizza("fuorifascia");

        assertEquals(21, analisi.punteggio());
        assertEquals("F4", analisi.fascia().codice());
        assertEquals(23, analisi.fascia().minimo());
        assertEquals(25, analisi.fascia().massimo());
        assertTrue(analisi.approssimato());
    }
}
