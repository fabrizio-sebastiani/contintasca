package it.hagenthon.backend.common;

import java.util.List;

/**
 * Eccezione applicativa che porta già lo stato HTTP e il codice d'errore del contratto.
 */
public class ApiException extends RuntimeException {

    private final int status;
    private final String codice;
    private final List<ErrorDetail> dettagli;

    public ApiException(int status, String codice, String messaggio, List<ErrorDetail> dettagli) {
        super(messaggio);
        this.status = status;
        this.codice = codice;
        this.dettagli = dettagli == null ? List.of() : dettagli;
    }

    public ApiException(int status, String codice, String messaggio) {
        this(status, codice, messaggio, List.of());
    }

    public int getStatus() {
        return status;
    }

    public String getCodice() {
        return codice;
    }

    public List<ErrorDetail> getDettagli() {
        return dettagli;
    }
}
