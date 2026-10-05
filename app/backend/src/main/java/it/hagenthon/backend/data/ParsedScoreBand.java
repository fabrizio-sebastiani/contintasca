package it.hagenthon.backend.data;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class ParsedScoreBand {
    public String codice;
    public int minimo;
    public int massimo;
    public int riga;
    public final Map<Voce, BigDecimal> importi = new EnumMap<>(Voce.class);
    /** riga fisica di ogni SPESA accettata, per poterla scartare se la fascia risulta incompleta. */
    public final Map<Voce, Integer> righeImporti = new EnumMap<>(Voce.class);
}
