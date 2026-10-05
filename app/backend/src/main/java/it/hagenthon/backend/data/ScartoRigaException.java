package it.hagenthon.backend.data;

/** Eccezione interna al parser: una riga viene scartata con questo codice motivo. */
class ScartoRigaException extends RuntimeException {
    final String codice;

    ScartoRigaException(String codice) {
        super(codice);
        this.codice = codice;
    }
}
