package it.hagenthon.backend.profile;

import java.util.List;
import java.util.Map;

/** {@code GET /api/profilo} (sezione 3.4 del contratto). */
public record ProfileResponse(String statoProfilo, List<DomandaDto> domande, List<String> voci,
        Map<String, String> risposte, Map<String, Integer> importi, String salvatoIl) {
}
