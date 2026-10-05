package it.hagenthon.backend.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.hagenthon.backend.common.ApiException;

/** Test unitari puri del parser CSV (nessun contesto Spring, nessun database). */
class CsvParserTest {

    private final CsvParser parser = new CsvParser();

    private static final String HEADER =
            "tipo_riga;domanda;tipo_risposta;etichetta;testo;spiegazione;opzione;minimo;massimo;punteggio;fascia;voce;importo";

    /** Un file minimo valido: 1 domanda a scelta, 1 fascia di punteggio completa. */
    private static String baseCsv() {
        return HEADER + "\n"
                + "DOMANDA;Q1;SCELTA;Q1;Testo domanda;;;;;;;;\n"
                + "OPZIONE;Q1;;;Opzione1;;OPZ1;;;1;;;\n"
                + "FASCIA_PUNTEGGIO;;;;;;;0;10;;F1;;\n"
                + "SPESA;;;;;;;;;;F1;CASA;1000\n"
                + "SPESA;;;;;;;;;;F1;SPORT_TEMPO_LIBERO;1000\n"
                + "SPESA;;;;;;;;;;F1;AUTO_MOBILITA;1000\n"
                + "SPESA;;;;;;;;;;F1;UTENZE;1000\n"
                + "SPESA;;;;;;;;;;F1;SPESA;1000\n";
    }

    private static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void rigaValida() {
        CsvParseResult result = parser.parse(bytes(baseCsv()));
        assertEquals(8, result.righeLette);
        assertEquals(8, result.righeImportate);
        assertEquals(0, result.righeScartate);
        assertEquals(1, result.domande.size());
        assertEquals(1, result.fasce.size());
    }

    @Test
    void campoMancante() {
        String csv = baseCsv() + "DOMANDA;Q2;SCELTA;Etichetta;;;;;;;;;\n";
        CsvParseResult result = parser.parse(bytes(csv));
        assertEquals(1, result.righeScartate);
        assertEquals(new CsvScarto(10, "CAMPO_MANCANTE"), result.scarti.get(0));
    }

    @Test
    void voceSconosciuta() {
        String csv = baseCsv() + "SPESA;;;;;;;;;;F1;VACANZE;500\n";
        CsvParseResult result = parser.parse(bytes(csv));
        assertEquals(1, result.righeScartate);
        assertEquals(new CsvScarto(10, "VOCE_SCONOSCIUTA"), result.scarti.get(0));
        assertEquals(1, result.fasce.size());
    }

    @Test
    void decimaleConVirgola() {
        String csv = HEADER + "\n"
                + "DOMANDA;Q1;SCELTA;Q1;Testo domanda;;;;;;;;\n"
                + "OPZIONE;Q1;;;Opzione1;;OPZ1;;;1;;;\n"
                + "FASCIA_PUNTEGGIO;;;;;;;0;10;;F1;;\n"
                + "SPESA;;;;;;;;;;F1;CASA;4200,50\n"
                + "SPESA;;;;;;;;;;F1;SPORT_TEMPO_LIBERO;1000\n"
                + "SPESA;;;;;;;;;;F1;AUTO_MOBILITA;1000\n"
                + "SPESA;;;;;;;;;;F1;UTENZE;1000\n"
                + "SPESA;;;;;;;;;;F1;SPESA;1000\n";
        CsvParseResult result = parser.parse(bytes(csv));
        assertEquals(0, result.righeScartate);
        BigDecimal casa = result.fasce.get(0).importi.get(Voce.CASA);
        assertEquals(0, casa.compareTo(new BigDecimal("4200.50")));
    }

    @Test
    void importoNegativo() {
        String csv = baseCsv() + "SPESA;;;;;;;;;;F1;CASA;-300\n";
        CsvParseResult result = parser.parse(bytes(csv));
        assertEquals(1, result.righeScartate);
        assertEquals(new CsvScarto(10, "VALORE_NEGATIVO"), result.scarti.get(0));
    }

    @Test
    void fasciaSovrapposta() {
        String csv = baseCsv() + "FASCIA_PUNTEGGIO;;;;;;;5;15;;F2;;\n";
        CsvParseResult result = parser.parse(bytes(csv));
        assertEquals(1, result.righeScartate);
        assertEquals(new CsvScarto(10, "FASCIA_SOVRAPPOSTA"), result.scarti.get(0));
        assertEquals(1, result.fasce.size());
    }

    @Test
    void fasciaIncompleta() {
        String csv = baseCsv()
                + "FASCIA_PUNTEGGIO;;;;;;;11;20;;F2;;\n"
                + "SPESA;;;;;;;;;;F2;CASA;500\n"
                + "SPESA;;;;;;;;;;F2;SPORT_TEMPO_LIBERO;500\n"
                + "SPESA;;;;;;;;;;F2;AUTO_MOBILITA;500\n"
                + "SPESA;;;;;;;;;;F2;UTENZE;500\n";
        CsvParseResult result = parser.parse(bytes(csv));
        assertEquals(5, result.righeScartate);
        assertTrue(result.scarti.stream().allMatch(s -> s.codice().equals("FASCIA_INCOMPLETA")));
        assertEquals(1, result.fasce.size());
    }

    @Test
    void fileVuoto() {
        ApiException ex = assertThrows(ApiException.class, () -> parser.parse(bytes("   \n\n  ")));
        assertEquals("FILE_VUOTO", ex.getCodice());
    }

    @Test
    void intestazioneNonValida() {
        ApiException ex = assertThrows(ApiException.class,
                () -> parser.parse(bytes("tipo;domanda\nDOMANDA;Q1")));
        assertEquals("FILE_INTESTAZIONE_NON_VALIDA", ex.getCodice());
    }

    @Test
    void soloIntestazioneDaSenzaDatiValidi() {
        ApiException ex = assertThrows(ApiException.class, () -> parser.parse(bytes(HEADER + "\n")));
        assertEquals("FILE_SENZA_DATI_VALIDI", ex.getCodice());
    }

    @Test
    void esempioValido() throws IOException {
        byte[] content = readResource("/esempio_istat.csv");
        CsvParseResult result = parser.parse(content);
        assertEquals(66, result.righeLette);
        assertEquals(66, result.righeImportate);
        assertEquals(0, result.righeScartate);
        assertEquals(9, result.domande.size());
        assertEquals(4, result.fasce.size());
    }

    @Test
    void esempioErrori() throws IOException {
        byte[] content = readResource("/esempio_istat_errori.csv");
        CsvParseResult result = parser.parse(content);
        assertEquals(79, result.righeLette);
        assertEquals(66, result.righeImportate);
        assertEquals(13, result.righeScartate);
        assertEquals(9, result.domande.size());
        assertEquals(4, result.fasce.size());

        List<CsvScarto> attesi = List.of(
                new CsvScarto(68, "CAMPO_MANCANTE"),
                new CsvScarto(69, "RIF_DOMANDA_INESISTENTE"),
                new CsvScarto(70, "FASCIA_SOVRAPPOSTA"),
                new CsvScarto(71, "NUMERO_NON_VALIDO"),
                new CsvScarto(72, "VOCE_SCONOSCIUTA"),
                new CsvScarto(73, "TIPO_RIGA_SCONOSCIUTO"),
                new CsvScarto(74, "NUMERO_COLONNE_ERRATO"),
                new CsvScarto(75, "FASCIA_INCOMPLETA"),
                new CsvScarto(76, "VALORE_NEGATIVO"),
                new CsvScarto(77, "FASCIA_INCOMPLETA"),
                new CsvScarto(78, "FASCIA_INCOMPLETA"),
                new CsvScarto(79, "FASCIA_INCOMPLETA"),
                new CsvScarto(80, "FASCIA_INCOMPLETA"));
        assertEquals(attesi, result.scarti);
    }

    private byte[] readResource(String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            return in.readAllBytes();
        }
    }
}
