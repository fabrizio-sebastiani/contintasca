package it.hagenthon.backend.data;

import java.util.List;

/** {@code POST /api/dati} (sezione 3.3 del contratto). */
public record CaricamentoResponse(int righeLette, int righeImportate, int righeScartate, List<CsvScarto> scarti,
        DatiAttualiResponse datiAttuali) {
}
