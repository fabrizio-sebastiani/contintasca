package it.hagenthon.backend.common;

import java.util.List;
import java.util.Locale;

/**
 * Normalizzazione e controllo del nome utente (RB-01).
 */
public final class UserNames {

    public static final int MASSIMO = 50;

    private UserNames() {
    }

    /** Trim + minuscolo con Locale.ROOT. Null diventa stringa vuota. */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Normalizza e controlla la lunghezza (1-50). Usato dai parametri di query
     * {@code nomeUtente}: se non valido lancia {@code 400 RICHIESTA_NON_VALIDA}.
     */
    public static String requireValid(String raw) {
        String normalizzato = normalize(raw);
        if (normalizzato.isEmpty() || normalizzato.length() > MASSIMO) {
            throw new ApiException(400, "RICHIESTA_NON_VALIDA", "nomeUtente non valido", List.of());
        }
        return normalizzato;
    }
}
