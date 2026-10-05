package it.hagenthon.backend.analysis;

public record VoceConfrontoDto(String voce, int spesaCorrente, int spesaCampione, int differenza,
        int differenzaAssoluta, Integer percentuale, String esito, String fraseCifra) {
}
