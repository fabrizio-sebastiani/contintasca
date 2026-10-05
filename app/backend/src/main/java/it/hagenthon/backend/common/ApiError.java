package it.hagenthon.backend.common;

import java.util.List;

/**
 * Formato d'errore unico (sezione 2 del contratto API).
 */
public record ApiError(String codice, String messaggio, List<ErrorDetail> dettagli) {
}
