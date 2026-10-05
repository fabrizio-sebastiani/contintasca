package it.hagenthon.backend.data;

/** {@code GET /api/dati} (sezione 3.2 del contratto). */
public record DatiAttualiResponse(boolean caricato, String nomeFile, String caricatoIl, int numeroDomande,
        int numeroFasce) {
}
