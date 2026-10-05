package it.hagenthon.backend.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formato delle date del contratto: {@code yyyy-MM-ddTHH:mm:ss}, ora locale del server.
 */
public final class Timestamps {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private Timestamps() {
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATO);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
