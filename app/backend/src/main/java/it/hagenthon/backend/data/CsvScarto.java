package it.hagenthon.backend.data;

/** Una riga scartata durante l'importazione: numero di riga fisica e codice motivo. */
public record CsvScarto(int riga, String codice) {
}
