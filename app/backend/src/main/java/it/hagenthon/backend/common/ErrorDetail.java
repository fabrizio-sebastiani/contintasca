package it.hagenthon.backend.common;

/**
 * Un dettaglio di errore di validazione (sezione 2 del contratto API).
 */
public record ErrorDetail(String campo, String codice, Integer minimo, Integer massimo) {
}
