package it.hagenthon.backend.profile;

import java.util.List;

public record DomandaDto(String codice, String tipo, String etichetta, String testo, String spiegazione,
        List<OpzioneDto> opzioni, Integer minimo, Integer massimo) {
}
