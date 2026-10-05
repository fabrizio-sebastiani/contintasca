package it.hagenthon.backend.data;

import java.util.ArrayList;
import java.util.List;

public class CsvParseResult {
    public int righeLette;
    public int righeImportate;
    public int righeScartate;
    public final List<CsvScarto> scarti = new ArrayList<>();
    public final List<ParsedQuestion> domande = new ArrayList<>();
    public final List<ParsedScoreBand> fasce = new ArrayList<>();
}
