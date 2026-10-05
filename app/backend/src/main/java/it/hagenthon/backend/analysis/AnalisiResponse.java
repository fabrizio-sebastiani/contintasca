package it.hagenthon.backend.analysis;

import java.util.List;

/** {@code GET /api/analisi} (sezione 3.6 del contratto). */
public record AnalisiResponse(int punteggio, FasciaDto fascia, boolean approssimato,
        List<RispostaMostrataDto> risposte, List<VoceConfrontoDto> voci, VoceConfrontoDto totale,
        int scalaMassima) {
}
