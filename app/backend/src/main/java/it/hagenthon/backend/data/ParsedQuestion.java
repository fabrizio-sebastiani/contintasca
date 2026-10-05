package it.hagenthon.backend.data;

import java.util.ArrayList;
import java.util.List;

public class ParsedQuestion {
    public String codice;
    public QuestionType tipo;
    public String etichetta;
    public String testo;
    public String spiegazione;
    public int ordine;
    public int riga;
    public final List<ParsedOption> opzioni = new ArrayList<>();
    public final List<ParsedRange> fasceRisposta = new ArrayList<>();
}
